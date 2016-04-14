package com.mjrichardson.teamCity.buildTriggers.MachineAdded;


import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class MachinesProviderFactoryTest {
    public void get_provider_returns_machines_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory sut = new MachinesProviderFactory(new FakeAnalyticsTracker(), new FakeCacheManager());
        String url = "the-url";
        String apiKey = "the-api-key";
        Integer connectionTimeoutInMilliseconds = 100;
        MachinesProvider result = sut.getProvider(url, apiKey, connectionTimeoutInMilliseconds);

        Assert.assertEquals(result.getClass(), MachinesProviderImpl.class);
    }
}
