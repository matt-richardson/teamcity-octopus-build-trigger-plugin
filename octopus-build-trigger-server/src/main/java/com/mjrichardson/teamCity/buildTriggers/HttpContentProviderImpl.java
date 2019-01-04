
package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidCacheConfigurationException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.UnexpectedResponseCodeException;
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
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

public class HttpContentProviderImpl implements HttpContentProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(HttpContentProviderImpl.class.getName());
    private final String octopusUrl;
    private final String apiKey;
    @NotNull
    private final Integer connectionTimeoutInMilliseconds;
    @NotNull
    private final CacheManager cacheManager;
    @NotNull
    private final MetricRegistry metricRegistry;

    public HttpContentProviderImpl(String octopusUrl, String apiKey, @NotNull Integer connectionTimeoutInMilliseconds, CacheManager cacheManager, MetricRegistry metricRegistry) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.octopusUrl = octopusUrl;
        this.apiKey = apiKey;
        this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
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
    public String getOctopusContent(CacheManager.CacheNames cacheName, @NotNull String uriPath, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        final URI uri = new URL(octopusUrl + uriPath).toURI();

        HashMap<String,String> headers = new HashMap<>();
        headers.put("X-Octopus-ApiKey", this.apiKey);

        try {
            return getContent(cacheName, uri, headers, correlationId);
        } catch (UnknownHostException e) {
            LOG.warn(String.format("%s: Unknown host exception while getting response from %s", correlationId, uri), e);
            throw new InvalidOctopusUrlException(uri, e);
        }
        catch (UnexpectedResponseCodeException ex) {
            final int statusCode = ex.code;
            if (statusCode == 401) {
                throw new InvalidOctopusApiKeyException(statusCode, ex.reason);
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
            throw ex;
        }
    }

    @NotNull
    public String getContent(CacheManager.CacheNames cacheName, @NotNull URI uri, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        return getContent(cacheName, uri, new HashMap<>(), correlationId);
    }

    @NotNull
    private String getContent(CacheManager.CacheNames cacheName, @NotNull URI uri, HashMap<String, String> headers, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        metricRegistry.meter(name(HttpContentProviderImpl.class, "apiRequests", "hits")).mark();
        metricRegistry.meter(name(HttpContentProviderImpl.class, "apiRequests", cacheName.name(), "hits")).mark();

        final String cachedResponse = cacheManager.getFromCache(cacheName, uri, correlationId);
        if (cachedResponse != null)
            return cachedResponse;

        final Timer.Context context = metricRegistry.timer(name(HttpContentProviderImpl.class, "apiRequests", cacheName.name(), "time")).time();
        final HttpGet httpGet = new HttpGet(uri);
        final CloseableHttpClient httpClient = getHttpClient(this.connectionTimeoutInMilliseconds);
        CloseableHttpResponse response = null;

        try {
            LOG.debug(String.format("%s: Getting response from url %s", correlationId, uri));
            for (Object key : headers.keySet()) {
                httpGet.addHeader((String)key, headers.get(key));
            }

            final HttpContext httpContext = HttpClientContext.create();
            response = httpClient.execute(httpGet, httpContext);

            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 300) {
                throw new UnexpectedResponseCodeException(statusCode, response.getStatusLine().getReasonPhrase());
            }

            final HttpEntity entity = response.getEntity();
            final String content = EntityUtils.toString(entity);
            LOG.debug(String.format("%s: request to %s returned %s", correlationId, uri, content));
            cacheManager.addToCache(cacheName, uri, content, correlationId);
            return content;
        } catch (Exception e) {
            LOG.warn(String.format("%s: Exception while getting response from %s", correlationId, uri), e);
            throw e;
        } finally {
            context.stop();
            httpGet.releaseConnection();
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOG.warn(String.format("%s: Exception while calling httpClient.close() - not much we can do", correlationId), e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                }
                catch (IOException e) {
                    LOG.warn(String.format("%s: Exception while calling response.close() - not much we can do", correlationId), e);
                }
            }
        }
    }
}
