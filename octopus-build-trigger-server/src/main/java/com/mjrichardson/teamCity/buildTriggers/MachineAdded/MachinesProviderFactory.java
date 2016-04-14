package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class MachinesProviderFactory {
    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;

    public MachinesProviderFactory(AnalyticsTracker analyticsTracker, CacheManager cacheManager) {
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
    }

    public MachinesProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout, cacheManager);
        return new MachinesProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
