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

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public class HttpContentProviderImpl implements HttpContentProvider {
  CloseableHttpClient httpClient;
  @Nullable
  private String apiKey;

    final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionTimeout).setSocketTimeout(connectionTimeout).build();
  public void init(@Nullable String apiKey, @NotNull Integer connectionTimeout) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    this.apiKey = apiKey;

    final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

    final HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslConnectionFactory);

    httpClient = builder.build();
  }

  public void close(@Nullable Closeable closeable) {
    if (closeable == null) return;
    try {
      closeable.close();
    } catch (IOException e) {
      //
    }
  }

  @NotNull
  public String getContent(@NotNull URI uri) throws IOException, UnexpectedResponseCodeException {
    final HttpGet httpGet = new HttpGet(uri);

    try {

      final HttpContext httpContext = HttpClientContext.create();
      httpGet.addHeader("X-Octopus-ApiKey", this.apiKey);
      final CloseableHttpResponse response = httpClient.execute(httpGet, httpContext);

      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode >= 300) {
        throw new UnexpectedResponseCodeException(statusCode, response.getStatusLine().getReasonPhrase());
      }

      final HttpEntity entity = response.getEntity();
      final String content = EntityUtils.toString(entity);
      return content;

    } finally {
      httpGet.releaseConnection();
    }
  }

}
