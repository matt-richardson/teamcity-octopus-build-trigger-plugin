package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class DeploymentsProviderFactory {
    public DeploymentsProvider getProvider(String octopusUrl, String octopusApiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new DeploymentsProviderImpl(octopusUrl, octopusApiKey, connectionTimeout);
    }
}
