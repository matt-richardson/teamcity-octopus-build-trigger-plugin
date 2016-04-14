package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProvider;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeDeploymentsProviderFactory extends DeploymentsProviderFactory {
    private final DeploymentsProvider deploymentsProvider;

    public FakeDeploymentsProviderFactory(DeploymentsProvider deploymentsProvider) {
        super(new FakeAnalyticsTracker(), new FakeCacheManager());
        this.deploymentsProvider = deploymentsProvider;
    }

    @Override
    public DeploymentsProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return deploymentsProvider;
    }
}
