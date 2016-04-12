package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class MachineAddedSpecCheckResultTest {
    public void create_empty_result_returns_an_object_with_no_updates_nor_errors() {
        MachineAddedSpecCheckResult result = MachineAddedSpecCheckResult.createEmptyResult();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void create_updated_result_returns_an_object_with_updates_but_no_errors() {
        Machine machine = new Machine("the-machine-id", "the-machine-name", new String[] { "env-id" }, new String[]{ "role-name" });
        MachineAddedSpec machineCreatedSpec = new MachineAddedSpec("the-id", machine);
        MachineAddedSpecCheckResult result = MachineAddedSpecCheckResult.createUpdatedResult(machineCreatedSpec);
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertTrue(result.updatesDetected());
        MachineAddedSpec[] array = result.getUpdated().toArray(new MachineAddedSpec[1]);
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], machineCreatedSpec);
    }

    public void create_throwable_result_returns_an_object_with_errors_but_no_updates() {
        Throwable throwable = new OutOfMemoryError("out of memory exception");
        MachineAddedSpecCheckResult result = MachineAddedSpecCheckResult.createThrowableResult(throwable);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError(), throwable);
        Assert.assertEquals(result.getGeneralError().getMessage(), "out of memory exception");
    }

    public void create_error_result_returns_an_object_with_errors_but_no_updates() {
        String error = "an error";
        MachineAddedSpecCheckResult result = MachineAddedSpecCheckResult.createErrorResult(error);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError().getClass(), BuildTriggerException.class);
        Assert.assertEquals(result.getGeneralError().getMessage(), "an error");
    }
}
