package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.*;

@Test
public class DeploymentProcessChangedAsyncBuildTriggerTest {
    @Test(expectedExceptions = BuildTriggerException.class,
            expectedExceptionsMessageRegExp = "the display name failed with error: the exception message")
    public void make_trigger_exception_throws_build_trigger_exception() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        sut.makeTriggerException(new ParseException("the exception message"));
    }

    public void get_requestor_string_returns_requestor_string_from_deployment_complete_spec() {
        String displayName = "the display name";
        int pollIntervalInSeconds = 100;
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        String version = "17";
        String projectId = "the-project-id";

        String result = sut.getRequestorString(new DeploymentProcessChangedSpec("the-url", version, projectId));

        Assert.assertEquals(result, "Deployment process of the-project-id has changed to version 17 on the-url");
    }

    public void poll_interval_returns_passed_in_poll_interval() {
        String displayName = "the display name";
        FakeBuildTriggerProperties buildTriggerProperties = new FakeBuildTriggerProperties();
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), buildTriggerProperties);
        int result = sut.getPollIntervalInMilliseconds();

        Assert.assertEquals(result, buildTriggerProperties.getPollInterval());
    }

    public void create_job_returns_instance_of_deployment_complete_check_job() throws CheckJobCreationException {
        String displayName = "the display name";
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        UUID correlationId = UUID.randomUUID();
        CheckJob<DeploymentProcessChangedSpec> result = sut.createJob(new FakeBuildType(),
                new FakeCustomDataStorage(),
                new FakeBuildTriggerDescriptor().getProperties(),
                correlationId);

        Assert.assertEquals(result.getClass(), DeploymentProcessChangedCheckJob.class);
    }

    public void create_crash_on_submit_result_returns_deployment_process_changed_spec_check_result() {
        String displayName = "the display name";
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.createCrashOnSubmitResult(new ParseException("the exception message"), correlationId);

        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertEquals(result.getGeneralError().getMessage(), "the exception message");
    }

    public void describe_trigger_returns_description_based_on_properties() {
        String displayName = "the display name";
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");

        String result = sut.describeTrigger(new FakeBuildTriggerDescriptor(hashMap));

        Assert.assertEquals(result, "Wait for a change in the deployment process of the-project on server the-server.");
    }

    public void get_properties_returns_expected_properties() {
        String displayName = "the display name";
        DeploymentProcessChangedAsyncBuildTrigger sut = new DeploymentProcessChangedAsyncBuildTrigger(displayName, new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        String version = "the-version";
        String projectId = "the-project-id";
        DeploymentProcessChangedSpec spec = new DeploymentProcessChangedSpec("the-url", version, projectId);
        Map<String, String> result = sut.getProperties(spec);

        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_PROCESS_VERSION), "the-version");
        Assert.assertEquals(result.get(BUILD_PROPERTY_DEPLOYMENT_PROCESS_PROJECT_ID), "the-project-id");
    }
}
