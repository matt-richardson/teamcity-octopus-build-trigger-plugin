package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class DeploymentsProviderFactory {

    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;

    public DeploymentsProviderFactory(AnalyticsTracker analyticsTracker, CacheManager cacheManager) {
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
    }

    public DeploymentsProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout, cacheManager);
        return new DeploymentsProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
