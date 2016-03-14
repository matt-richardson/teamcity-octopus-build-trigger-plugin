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

package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpContentProviderImpl implements HttpContentProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(HttpContentProviderImpl.class.getName());

    private final String octopusUrl;
    ;
    @NotNull
    private String apiKey;
    @NotNull
    private final Integer connectionTimeoutInMilliseconds;

    public HttpContentProviderImpl(@NotNull String octopusUrl, @NotNull String apiKey, @NotNull Integer connectionTimeoutInMilliseconds) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.octopusUrl = octopusUrl;
        this.apiKey = apiKey;
        this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
    }

    private CloseableHttpClient getHttpClient(@NotNull Integer connectionTimeoutInMilliseconds) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeoutInMilliseconds)
                .setConnectionRequestTimeout(connectionTimeoutInMilliseconds)
                .setSocketTimeout(connectionTimeoutInMilliseconds)
                .build();

        final SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        final HttpClientBuilder builder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(sslConnectionFactory);

        return builder.build();
    }

    public String getUrl() {
        return this.octopusUrl;
    }

    @NotNull
    public String getContent(@NotNull String uriPath) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final URI uri = new URL(octopusUrl + uriPath).toURI();
        final HttpGet httpGet = new HttpGet(uri);
        CloseableHttpClient httpClient = getHttpClient(this.connectionTimeoutInMilliseconds);

        try {
            LOG.info("Getting response from url " + uri);
            final HttpContext httpContext = HttpClientContext.create();
            httpGet.addHeader("X-Octopus-ApiKey", this.apiKey);

            final CloseableHttpResponse response = httpClient.execute(httpGet, httpContext);

            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 401) {
                throw new InvalidOctopusApiKeyException(statusCode, response.getStatusLine().getReasonPhrase());
            }
            if (statusCode == 404 && uriPath.matches(".*Projects-\\d*")) {
                Pattern p = Pattern.compile(".*(Projects-\\d*)$");
                Matcher m = p.matcher(uriPath);
                m.find();
                throw new ProjectNotFoundException(m.group(1));
            }
            if (statusCode == 404) {
                throw new InvalidOctopusUrlException(uri);
            }
            if (statusCode >= 300) {
                throw new UnexpectedResponseCodeException(statusCode, response.getStatusLine().getReasonPhrase());
            }

            final HttpEntity entity = response.getEntity();
            final String content = EntityUtils.toString(entity);
            LOG.info("request to " + uri + " returned " + content);
            return content;
        } catch (UnknownHostException e) {
            LOG.warn("Unknown host exception while getting response from " + uri);
            throw new InvalidOctopusUrlException(uri, e);
        } catch (Exception e) {
            LOG.warn("Exception while getting response from " + uri);
            throw e;
        } finally {
            httpGet.releaseConnection();
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOG.warn("Exception while calling httpClient.close() - not much we can do", e);
                }
            }
        }
    }
}
