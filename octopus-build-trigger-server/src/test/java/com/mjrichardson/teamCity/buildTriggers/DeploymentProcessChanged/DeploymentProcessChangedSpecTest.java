package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DeploymentProcessChangedSpecTest {
    public void when_successful_returns_success_message() throws Exception {
        String version = "18";
        String projectId = "the-project-id";
        DeploymentProcessChangedSpec DeploymentProcessChangedSpec = new DeploymentProcessChangedSpec("the-url", version, projectId);
        Assert.assertEquals(DeploymentProcessChangedSpec.getRequestorString(), "Deployment process of the-project-id has changed to version 18 on the-url");
    }

    public void to_string_converts_correctly() {
        String version = "18";
        String projectId = "the-project-id";
        DeploymentProcessChangedSpec DeploymentProcessChangedSpec = new DeploymentProcessChangedSpec("the-url", version, projectId);
        String result = DeploymentProcessChangedSpec.toString();
        Assert.assertEquals(result, "{ url: 'the-url', projectId: 'the-project-id', version: '18' }");
    }
}
