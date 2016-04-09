package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAsyncTriggerParameters;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

@Test
public class MachineAddedAsyncBuildTriggerTest {
    @Test(expectedExceptions = BuildTriggerException.class,
            expectedExceptionsMessageRegExp = "the display name failed with error: the exception message")
    public void make_trigger_exception_throws_build_trigger_exception() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        sut.makeTriggerException(new ParseException("the exception message"));
    }

    public void get_requestor_string_returns_requestor_string_from_deployment_complete_spec() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        Machine machine = new Machine("the-machine-id", "the-machine-name");
        String result = sut.getRequestorString(new MachineAddedSpec("the-url", machine));

        Assert.assertEquals(result, "Machine the-machine-name added to the-url");
    }

    public void poll_interval_returns_passed_in_poll_interval() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        Integer result = sut.getPollInterval(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result, pollIntervalInSeconds);
    }

    public void create_job_returns_instance_of_deployment_complete_check_job() throws CheckJobCreationException {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        CheckJob<MachineAddedSpec> result = sut.createJob(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result.getClass(), MachineAddedCheckJob.class);
    }

    public void create_crash_on_submit_result_returns_deployment_complete_spec_check_result() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.createCrashOnSubmitResult(new ParseException("the exception message"));

        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertEquals(result.getGeneralError().getMessage(), "the exception message");
    }

    public void describe_trigger_returns_description_based_on_properties() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new machine to be added to server the-server.");
    }

    public void describe_trigger_returns_description_based_on_properties2() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");
        hashMap.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new machine to be added to server the-server.");
    }

    public void get_properties_returns_expected_properties() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        MachineAddedAsyncBuildTrigger sut = new MachineAddedAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());

        String[] environmentIds = new String[1];
        environmentIds[0] = "environment-1";
        String[] roleIds = new String[2];
        roleIds[0] = "role-one";
        roleIds[1] = "role-two";

        Machine machine = new Machine("the-machine-id", "the-machine-name", environmentIds, roleIds);

        MachineAddedSpec spec = new MachineAddedSpec("the-url", machine);
        Map<String, String> result = sut.getProperties(spec);

        Assert.assertEquals(result.get(BUILD_PROPERTY_MACHINE_ID), "the-machine-id");
        Assert.assertEquals(result.get(BUILD_PROPERTY_MACHINE_NAME), "the-machine-name");
        Assert.assertEquals(result.get(BUILD_PROPERTY_MACHINE_ENVIRONMENT_IDS), "environment-1");
        Assert.assertEquals(result.get(BUILD_PROPERTY_MACHINE_ROLE_IDS), "role-one,role-two");
    }
}
