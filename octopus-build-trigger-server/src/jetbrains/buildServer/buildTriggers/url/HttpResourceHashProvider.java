/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.buildTriggers.url;

import jetbrains.buildServer.util.StringUtil;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

/**
 * User: Victory.Bedrosova
 * Date: 10/2/12
 * Time: 4:50 PM
 */
final class HttpResourceHashProvider implements ResourceHashProvider {

  @NotNull
  String getResourceHash(@NotNull String url) throws ResourceHashProviderException, IOException, NoSuchAlgorithmException, KeyManagementException {
    return getResourceHash(TriggerParameters.create(url));
  }

  @NotNull
  public String getResourceHash(@NotNull TriggerParameters triggerParameters) throws ResourceHashProviderException {
    CloseableHttpClient httpClient = null;

    final String old = triggerParameters.getOldHash();

    try {
      final URI uri = new URL(triggerParameters.getURL()).toURI();
      final String username = triggerParameters.getUsername();
      final String password = triggerParameters.getPassword();
      final Integer connectionTimeout = triggerParameters.getConnectionTimeout();

      httpClient = createClient(uri, username, password, connectionTimeout);

      // Let's try HEAD request, check headers
      Map<String, String> map = null;
      if (!StringUtil.isEmptyOrSpaces(old)) {
        if (old.startsWith("W/") || old.startsWith("\"")) {
          // Seems it's ETag
          map = Collections.singletonMap(HttpHeaders.IF_NONE_MATCH, old);
        } else if (DateUtils.parseDate(old) != null) {
          // It's date from Last-Modified
          map = Collections.singletonMap(HttpHeaders.IF_MODIFIED_SINCE, old);
        }
      }
      final Header[] headers = getHttpHeaders(httpClient, uri, map);

      final String eTag = getFirstHeader(headers, HttpHeaders.ETAG);
      if (!StringUtil.isEmptyOrSpaces(eTag)) {
        return eTag;
      }
      final String lastModified = getFirstHeader(headers, HttpHeaders.LAST_MODIFIED);
      if (!StringUtil.isEmptyOrSpaces(lastModified)) {
        return lastModified;
      }
      // Perform GET request, calculate content hash
      return getContentHash(httpClient, uri);

    } catch (UnexpectedResponseCode e) {
      if (e.code == HttpStatus.SC_NOT_MODIFIED && old != null) {
        // Not modified, yay!
        return old;
      }
      throw new ResourceHashProviderException("URL " + triggerParameters.getURL() + ": " + e.getMessage(), e);
    } catch (Throwable e) {
      throw new ResourceHashProviderException("URL " + triggerParameters.getURL() + ": " + e.getMessage(), e);

    } finally {
      close(httpClient);
    }
  }

  @Nullable
  private String getFirstHeader(@NotNull Header[] headers, @NotNull String name) {
    for (Header header : headers) {
      if (name.equals(header.getName())) {
        return header.getValue();
      }
    }
    return null;
  }

  private void close(@Nullable Closeable closeable) {
    if (closeable == null) return;
    try {
      closeable.close();
    } catch (IOException e) {
      //
    }
  }

  @NotNull
  private CloseableHttpClient createClient(@NotNull URI uri, @Nullable String username, @Nullable String password, @NotNull Integer connectionTimeout) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionTimeout).setSocketTimeout(connectionTimeout).build();

    final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

    final HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslConnectionFactory);

    if (StringUtil.isNotEmpty(username)) {

      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(new AuthScope(getHostName(uri), uri.getPort()), new UsernamePasswordCredentials(username, password));

      return builder.setDefaultCredentialsProvider(credentialsProvider).build();
    }

    return builder.build();
  }

  @NotNull
  private HttpContext createContext(@NotNull URI uri) {
    final HttpClientContext context = HttpClientContext.create();

    final AuthCache authCache = new BasicAuthCache();
    authCache.put(new HttpHost(getHostName(uri), uri.getPort()), new BasicScheme());
    context.setAuthCache(authCache);

    return context;
  }

  @NotNull
  private String getHostName(@NotNull URI uri) {
    final String host = uri.getHost();
    if (host.startsWith("http://")) return host.substring(7);
    if (host.startsWith("https://")) return host.substring(8);
    return host;
  }

  @NotNull
  private Header[] getHttpHeaders(@NotNull CloseableHttpClient httpClient, @NotNull URI uri,
                                  @Nullable Map<String, String> headers) throws IOException, ResourceHashProviderException {
    final HttpHead httpHead = new HttpHead(uri);
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        httpHead.setHeader(entry.getKey(), entry.getValue());
      }
    }
    try {
      final Header[] result = sendRequest(httpClient, httpHead, uri).getAllHeaders();
      return result != null ? result : new Header[0];
    } finally {
      httpHead.releaseConnection();
    }
  }

  @NotNull
  private String getContentHash(@NotNull CloseableHttpClient httpClient, @NotNull URI uri) throws ResourceHashProviderException, IOException {
    final HttpGet httpGet = new HttpGet(uri);

    try {

      final HttpEntity entity = sendRequest(httpClient, httpGet, uri).getEntity();
      return entity == null ? OctopusBuildTriggerUtil.UNEXITING_RESOURCE_HASH : OctopusBuildTriggerUtil.getDigest(entity.getContent());

    } finally {
      httpGet.releaseConnection();
    }
  }

  @NotNull
  private CloseableHttpResponse sendRequest(@NotNull CloseableHttpClient httpClient, @NotNull HttpUriRequest request, @NotNull URI uri) throws ResourceHashProviderException, IOException {
    final HttpContext httpContext = createContext(uri);
    final CloseableHttpResponse response = httpClient.execute(request, httpContext);

    final int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode >= 300) {
      throw new UnexpectedResponseCode(statusCode, response.getStatusLine().getReasonPhrase());
    }

    return response;
  }

  private static class UnexpectedResponseCode extends ResourceHashProviderException {
    public final int code;

    public UnexpectedResponseCode(int code, String reason) {
      super("Server returned " + code + " " + reason);
      this.code = code;
    }
  }
}