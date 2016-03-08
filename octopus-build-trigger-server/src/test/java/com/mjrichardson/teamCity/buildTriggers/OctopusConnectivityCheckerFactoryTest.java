package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OctopusConnectivityCheckerFactoryTest {
    public void create_returns_octopus_connectivity_checker() throws Exception {
        OctopusConnectivityCheckerFactory sut = new OctopusConnectivityCheckerFactory();
        Integer connectionTimeoutInMilliseconds = 100;
        OctopusConnectivityChecker result = sut.create("url", "apiKey", connectionTimeoutInMilliseconds);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getClass(), OctopusConnectivityChecker.class);
    }
}
