package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class DeploymentsProviderFactory {

    private final AnalyticsTracker analyticsTracker;

    public DeploymentsProviderFactory(AnalyticsTracker analyticsTracker) {
        this.analyticsTracker = analyticsTracker;
    }

    public DeploymentsProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout);
        return new DeploymentsProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
