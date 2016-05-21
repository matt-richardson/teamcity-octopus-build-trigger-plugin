package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;


import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class DeploymentsProviderFactoryTest {
    public void get_provider_returns_deployments_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory sut = new DeploymentsProviderFactory(new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry());
        String url = "the-url";
        String apiKey = "the-api-key";
        DeploymentsProvider result = sut.getProvider(url, apiKey, new FakeBuildTriggerProperties());

        Assert.assertEquals(result.getClass(), DeploymentsProviderImpl.class);
    }
}
