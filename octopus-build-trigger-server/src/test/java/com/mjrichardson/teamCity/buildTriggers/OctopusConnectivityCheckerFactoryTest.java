package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OctopusConnectivityCheckerFactoryTest {
    public void create_returns_octopus_connectivity_checker() throws Exception {
        OctopusConnectivityCheckerFactory sut = new OctopusConnectivityCheckerFactory(new FakeCacheManager());
        Integer connectionTimeoutInMilliseconds = 100;
        OctopusConnectivityChecker result = sut.create("url", "apiKey", connectionTimeoutInMilliseconds, new FakeMetricRegistry());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getClass(), OctopusConnectivityChecker.class);
    }
}
