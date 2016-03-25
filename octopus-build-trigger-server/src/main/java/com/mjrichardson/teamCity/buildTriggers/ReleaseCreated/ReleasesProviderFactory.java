package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class ReleasesProviderFactory {
    private AnalyticsTracker analyticsTracker;

    public ReleasesProviderFactory(AnalyticsTracker analyticsTracker) {
        this.analyticsTracker = analyticsTracker;
    }

    public ReleasesProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey, connectionTimeout);
        return new ReleasesProviderImpl(contentProviderFactory, analyticsTracker);
    }
}
