package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

@Test
public class DeploymentCompleteSpecCheckResultTest {
    public void create_empty_result_returns_an_object_with_no_updates_nor_errors() {
        UUID correlationId = UUID.randomUUID();
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createEmptyResult(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void create_updated_result_returns_an_object_with_updates_but_no_errors() {
        Environment environment = new Environment("the-env-id", new OctopusDate(2016,4,9), new OctopusDate(2016,4,9), "the-release-id", "the-deployment-id", "the-version", "the-project-id");

        DeploymentCompleteSpec deploymentCompleteSpec = new DeploymentCompleteSpec("the-url", environment);
        UUID correlationId = UUID.randomUUID();
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createUpdatedResult(deploymentCompleteSpec, correlationId);
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertTrue(result.updatesDetected());
        DeploymentCompleteSpec[] array = result.getUpdated().toArray(new DeploymentCompleteSpec[1]);
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], deploymentCompleteSpec);
    }

    public void create_throwable_result_returns_an_object_with_errors_but_no_updates() {
        Throwable throwable = new OutOfMemoryError("out of memory exception");
        UUID correlationId = UUID.randomUUID();
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createThrowableResult(throwable, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError(), throwable);
        Assert.assertEquals(result.getGeneralError().getMessage(), "out of memory exception");
    }

    public void create_error_result_returns_an_object_with_errors_but_no_updates() {
        String error = "an error";
        UUID correlationId = UUID.randomUUID();
        DeploymentCompleteSpecCheckResult result = DeploymentCompleteSpecCheckResult.createErrorResult(error, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError().getClass(), BuildTriggerException.class);
        Assert.assertEquals(result.getGeneralError().getMessage(), "an error");
    }
}
