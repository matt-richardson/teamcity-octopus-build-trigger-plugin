package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

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

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

@Test
public class ReleaseCreatedAsyncBuildTriggerTest {
    @Test(expectedExceptions = BuildTriggerException.class,
          expectedExceptionsMessageRegExp = "the display name failed with error: the exception message")
    public void make_trigger_exception_throws_build_trigger_exception() {
        String displayName = "the display name";
        int pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);
        sut.makeTriggerException(new ParseException("the exception message"));
    }

    public void get_requestor_string_returns_requestor_string_from_deployment_complete_spec() {
        String displayName = "the display name";
        int pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);
        String result = sut.getRequestorString(new ReleaseCreatedSpec("the-url", "the-project"));

        Assert.assertEquals(result, "Release of project the-project created on the-url");
    }

    public void poll_interval_returns_passed_in_poll_interval() {
        String displayName = "the display name";
        Integer pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);
        Integer result = sut.getPollInterval(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result, pollInterval);
    }

    public void create_job_returns_instance_of_deployment_complete_check_job() throws CheckJobCreationException {
        String displayName = "the display name";
        Integer pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);
        CheckJob<ReleaseCreatedSpec> result = sut.createJob(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result.getClass(), ReleaseCreatedCheckJob.class);
    }

    public void create_crash_on_submit_result_returns_deployment_complete_spec_check_result() {
        String displayName = "the display name";
        Integer pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);
        CheckResult<ReleaseCreatedSpec> result = sut.createCrashOnSubmitResult(new ParseException("the exception message"));

        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertEquals(result.getGeneralError().getMessage(), "the exception message");
    }

    public void describe_trigger_returns_description_based_on_properties() {
        String displayName = "the display name";
        Integer pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new release of the-project to be created on server the-server.");
    }

    public void describe_trigger_returns_description_based_on_properties2() {
        String displayName = "the display name";
        Integer pollInterval = 100;
        ReleaseCreatedAsyncBuildTrigger sut = new ReleaseCreatedAsyncBuildTrigger(displayName, pollInterval);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");
        hashMap.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new release of the-project to be created on server the-server.");
    }

}