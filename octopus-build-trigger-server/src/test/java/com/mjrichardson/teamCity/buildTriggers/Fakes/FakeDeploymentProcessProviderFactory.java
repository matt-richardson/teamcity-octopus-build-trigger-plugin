package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessProvider;
import com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeDeploymentProcessProviderFactory extends DeploymentProcessProviderFactory {
    private final DeploymentProcessProvider deploymentProcessProvider;

    public FakeDeploymentProcessProviderFactory(DeploymentProcessProvider deploymentProcessProvider) {
        super(new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry());
        this.deploymentProcessProvider = deploymentProcessProvider;
    }

    @Override
    public DeploymentProcessProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return deploymentProcessProvider;
    }
}
