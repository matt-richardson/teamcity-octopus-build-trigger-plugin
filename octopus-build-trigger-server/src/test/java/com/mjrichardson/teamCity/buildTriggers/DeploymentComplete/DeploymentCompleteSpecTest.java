package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DeploymentCompleteSpecTest {

    @Test
    public void two_param_ctor_returns_error_message() throws Exception {
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("theurl", "theproject");
        Assert.assertEquals(sut.getRequestorString(), "Unsuccessful attempt to get deployments for theproject on theurl");
    }

    @Test
    public void when_successful_returns_success_message() throws Exception {
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("theurl", "theproject", "theenv", true);
        Assert.assertEquals(sut.getRequestorString(), "Successful deployment of theproject to theenv on theurl");
    }

    @Test
    public void when_unsuccessful_returns_success_message() throws Exception {
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("theurl", "theproject", "theenv", false);
        Assert.assertEquals(sut.getRequestorString(), "Deployment of theproject to theenv on theurl");
    }
}
