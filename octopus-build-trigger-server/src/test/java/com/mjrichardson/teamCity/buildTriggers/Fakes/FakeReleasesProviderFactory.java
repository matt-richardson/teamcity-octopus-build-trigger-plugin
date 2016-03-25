package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProvider;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeReleasesProviderFactory extends ReleasesProviderFactory {
    private final ReleasesProvider releasesProvider;

    public FakeReleasesProviderFactory(ReleasesProvider releasesProvider) {
        super(new FakeAnalyticsTracker());
        this.releasesProvider = releasesProvider;
    }

    @Override
    public ReleasesProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return this.releasesProvider;
    }
}
