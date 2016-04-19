package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.codahale.metrics.MetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class DeploymentsProviderFactory {

    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;
    private MetricRegistry metricRegistry;

    public DeploymentsProviderFactory(AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry) {
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
    }

    public DeploymentsProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout, cacheManager, metricRegistry);
        return new DeploymentsProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
