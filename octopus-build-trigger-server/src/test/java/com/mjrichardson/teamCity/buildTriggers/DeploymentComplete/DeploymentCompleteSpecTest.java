package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DeploymentCompleteSpecTest {

    @Test
    public void when_successful_returns_success_message() throws Exception {
        Environment environment = new Environment("theenv", new OctopusDate(2016,4,9), new OctopusDate(2016,4,9), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("theurl", environment);
        Assert.assertEquals(sut.getRequestorString(), "Successful deployment of the-project-id to theenv on theurl");
    }

    @Test
    public void when_unsuccessful_returns_non_successful_deployment_message() throws Exception {
        Environment environment = new Environment("theenv", new OctopusDate(2016,4,9), new OctopusDate(2016,4,8), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("theurl", environment);
        Assert.assertEquals(sut.getRequestorString(), "Deployment of the-project-id to theenv on theurl");
    }

    public void to_string_converts_correctly() {
        Environment environment = new Environment("env-id", new OctopusDate(2016, 4, 8), new OctopusDate(2016, 4, 7), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        DeploymentCompleteSpec sut = new DeploymentCompleteSpec("the-url", environment);
        String result = sut.toString();
        Assert.assertEquals(result, "{ url: 'the-url', projectId: 'the-project-id', wasSuccessful: 'false', environmentId: 'env-id', deploymentId: 'the-deployment-id', version: 'the-version', releaseId: 'the-release-id' }");
    }
}
