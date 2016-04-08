package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
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

    public void to_string_converts_correctly() {
        Environment environment = new Environment("env-id", new OctopusDate(2016, 4, 8), new OctopusDate(2016, 4, 7), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("the-url", "the-project-id", environment);
        String result = sut.toString();
        Assert.assertEquals(result, "{ url: 'the-url', projectId: 'the-project-id', wasSuccessful: 'false', environmentId: 'env-id', deploymentId: 'the-deployment-id', version: 'the-version', releaseId: 'the-release-id' }");
    }
}
