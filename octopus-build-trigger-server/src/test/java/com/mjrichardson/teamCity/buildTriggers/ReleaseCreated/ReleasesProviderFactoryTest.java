package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;


import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class ReleasesProviderFactoryTest {
    public void get_provider_returns_releases_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory sut = new ReleasesProviderFactory(new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry());
        String url = "the-url";
        String apiKey = "the-api-key";
        Integer connectionTimeoutInMilliseconds = 100;
        ReleasesProvider result = sut.getProvider(url, apiKey, connectionTimeoutInMilliseconds);

        Assert.assertEquals(result.getClass(), ReleasesProviderImpl.class);
    }
}
