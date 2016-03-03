package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OctopusConnectivityCheckerFactoryTest {
    public void create_returns_octopus_connectivity_checker() throws Exception {
        OctopusConnectivityCheckerFactory sut = new OctopusConnectivityCheckerFactory();
        OctopusConnectivityChecker result = sut.create("url", "apiKey", 100);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getClass(), OctopusConnectivityChecker.class);
    }
}
