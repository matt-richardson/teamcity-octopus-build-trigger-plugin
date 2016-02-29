package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class DeploymentsProviderFactoryTest {
    public void get_provider_returns_deployments_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory sut = new DeploymentsProviderFactory();
        String url = "the-url";
        String apiKey = "the-api-key";
        Integer connectionTimeout = 100;
        DeploymentsProvider result = sut.getProvider(url, apiKey, connectionTimeout);

        Assert.assertEquals(result.getClass(), DeploymentsProviderImpl.class);
    }
}
