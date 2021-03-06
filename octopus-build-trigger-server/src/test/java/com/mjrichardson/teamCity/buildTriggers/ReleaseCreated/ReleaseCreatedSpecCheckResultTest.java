package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

@Test
public class ReleaseCreatedSpecCheckResultTest {
    public void create_empty_result_returns_an_object_with_no_updates_nor_errors() {
        UUID correlationId = UUID.randomUUID();
        ReleaseCreatedSpecCheckResult result = ReleaseCreatedSpecCheckResult.createEmptyResult(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void create_updated_result_returns_an_object_with_updates_but_no_errors() {
        Release release = new Release("the-release-id", new OctopusDate(2016, 4, 9), "the-version", "the-project-id");
        ReleaseCreatedSpec releaseCreatedSpec = new ReleaseCreatedSpec("the-url", release);
        UUID correlationId = UUID.randomUUID();
        ReleaseCreatedSpecCheckResult result = ReleaseCreatedSpecCheckResult.createUpdatedResult(releaseCreatedSpec, correlationId);
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertTrue(result.updatesDetected());
        ReleaseCreatedSpec[] array = result.getUpdated().toArray(new ReleaseCreatedSpec[1]);
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], releaseCreatedSpec);
    }

    public void create_throwable_result_returns_an_object_with_errors_but_no_updates() {
        Throwable throwable = new OutOfMemoryError("out of memory exception");
        UUID correlationId = UUID.randomUUID();
        ReleaseCreatedSpecCheckResult result = ReleaseCreatedSpecCheckResult.createThrowableResult(throwable, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError(), throwable);
        Assert.assertEquals(result.getGeneralError().getMessage(), "out of memory exception");
    }

    public void create_error_result_returns_an_object_with_errors_but_no_updates() {
        String error = "an error";
        UUID correlationId = UUID.randomUUID();
        ReleaseCreatedSpecCheckResult result = ReleaseCreatedSpecCheckResult.createErrorResult(error, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError().getClass(), BuildTriggerException.class);
        Assert.assertEquals(result.getGeneralError().getMessage(), "an error");
    }
}
