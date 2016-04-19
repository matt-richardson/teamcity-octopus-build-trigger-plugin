package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import org.jetbrains.annotations.NotNull;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HttpContentProviderFactory {

    @NotNull
    private final String octopusUrl;
    @NotNull
    private final String apiKey;
    @NotNull
    private final Integer connectionTimeout;
    @NotNull
    private final CacheManager cacheManager;
    @NotNull
    private MetricRegistry metricRegistry;

    public HttpContentProviderFactory(@NotNull String octopusUrl, @NotNull String apiKey, @NotNull Integer connectionTimeout, @NotNull CacheManager cacheManager, @NotNull MetricRegistry metricRegistry) {
        this.octopusUrl = octopusUrl;
        this.apiKey = apiKey;
        this.connectionTimeout = connectionTimeout;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
    }

    public HttpContentProvider getContentProvider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout, cacheManager, metricRegistry);
    }
}
