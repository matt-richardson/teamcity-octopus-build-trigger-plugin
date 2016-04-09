package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAsyncTriggerParameters;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerDescriptor;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
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
public class DeploymentCompleteAsyncBuildTriggerTest {
    @Test(expectedExceptions = BuildTriggerException.class,
            expectedExceptionsMessageRegExp = "the display name failed with error: the exception message")
    public void make_trigger_exception_throws_build_trigger_exception() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        sut.makeTriggerException(new ParseException("the exception message"));
    }

    public void get_requestor_string_returns_requestor_string_from_deployment_complete_spec() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        Environment environment = new Environment("the-env-id", new OctopusDate(2016,4,9), new OctopusDate(2016,4,9), "the-release-id", "the-deployment-id", "the-version", "the-project-id");

        String result = sut.getRequestorString(new DeploymentCompleteSpec("the-url", environment));

        Assert.assertEquals(result, "Successful deployment of the-project-id to the-env-id on the-url");
    }

    public void poll_interval_returns_passed_in_poll_interval() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        Integer result = sut.getPollInterval(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result, pollIntervalInSeconds);
    }

    public void create_job_returns_instance_of_deployment_complete_check_job() throws CheckJobCreationException {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        CheckJob<DeploymentCompleteSpec> result = sut.createJob(new FakeAsyncTriggerParameters());

        Assert.assertEquals(result.getClass(), DeploymentCompleteCheckJob.class);
    }

    public void create_crash_on_submit_result_returns_deployment_complete_spec_check_result() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        CheckResult<DeploymentCompleteSpec> result = sut.createCrashOnSubmitResult(new ParseException("the exception message"));

        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertEquals(result.getGeneralError().getMessage(), "the exception message");
    }

    public void describe_trigger_returns_description_based_on_properties() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new deployment of the-project on server the-server.");
    }

    public void describe_trigger_returns_description_based_on_properties_for_successful_deployment() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");
        hashMap.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a new successful deployment of the-project on server the-server.");
    }

    public void get_properties_returns_expected_properties() {
        String displayName = "the display name";
        Integer pollIntervalInSeconds = 100;
        DeploymentCompleteAsyncBuildTrigger sut = new DeploymentCompleteAsyncBuildTrigger(displayName, pollIntervalInSeconds, new FakeAnalyticsTracker());
        Environment environment = new Environment("the-environment-id", new OctopusDate(2016,4,9), new OctopusDate(2016,4,9), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        DeploymentCompleteSpec spec = new DeploymentCompleteSpec("the-url", environment);
        Map<String, String> result = sut.getProperties(spec);

        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_ID), "the-deployment-id");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_VERSION), "the-version");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_PROJECT_ID), "the-project-id");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_RELEASE_ID), "the-release-id");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_ENVIRONMENT_ID), "the-environment-id");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_SUCCESSFUL), "true");
    }
}
