package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DeploymentCompleteSpecCheckResultTest {
    public void create_empty_result_returns_an_object_with_no_updates_nor_errors() {
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createEmptyResult();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void create_updated_result_returns_an_object_with_updates_but_no_errors() {
        Environment environment = new Environment("the-env-id", new OctopusDate(2016,4,9), new OctopusDate(2016,4,9), "the-release-id", "the-deployment-id", "the-version", "the-project-id");

        DeploymentCompleteSpec deploymentCompleteSpec = new DeploymentCompleteSpec("the-url", "the-project", environment);
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createUpdatedResult(deploymentCompleteSpec);
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertTrue(result.updatesDetected());
        DeploymentCompleteSpec[] array = result.getUpdated().toArray(new DeploymentCompleteSpec[1]);
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], deploymentCompleteSpec);
    }

    public void create_throwable_result_returns_an_object_with_errors_but_no_updates() {
        Throwable throwable = new OutOfMemoryError("out of memory exception");
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createThrowableResult(throwable);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError(), throwable);
        Assert.assertEquals(result.getGeneralError().getMessage(), "out of memory exception");
    }

    public void create_error_result_returns_an_object_with_errors_but_no_updates() {
        String error = "an error";
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createErrorResult(error);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError().getClass(), BuildTriggerException.class);
        Assert.assertEquals(result.getGeneralError().getMessage(), "an error");
    }
}
