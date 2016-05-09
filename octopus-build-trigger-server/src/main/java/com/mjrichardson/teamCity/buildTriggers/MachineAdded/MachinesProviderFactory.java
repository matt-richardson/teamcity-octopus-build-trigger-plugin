package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.codahale.metrics.MetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class MachinesProviderFactory {
    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;
    private final MetricRegistry metricRegistry;

    public MachinesProviderFactory(AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry) {
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
    }

    public MachinesProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout, cacheManager, metricRegistry);
        return new MachinesProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
