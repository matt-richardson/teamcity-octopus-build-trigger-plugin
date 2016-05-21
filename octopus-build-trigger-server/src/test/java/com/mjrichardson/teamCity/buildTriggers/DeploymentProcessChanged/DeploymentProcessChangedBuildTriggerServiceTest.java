package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeploymentProcessChangedBuildTriggerServiceTest {

    @Test
    public void get_name_returns_trigger_internal_name() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.getName(), "octopusDeploymentProcessChangedTrigger");
    }

    @Test
    public void get_display_name_returns_user_friendly_name() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.getDisplayName(), "Octopus Deployment Process Changed Trigger");
    }

    @Test
    public void describe_trigger_returns_human_readable_string() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.describeTrigger(new FakeBuildTriggerDescriptor()), "Wait for a change in the deployment process of the-project on server the-server.");
    }

    @Test
    public void get_build_triggering_policy_returns_policy_provided_by_factory() throws Exception {
        FakeBuildTriggeringPolicy triggerPolicy = new FakeBuildTriggeringPolicy();
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(triggerPolicy), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.getBuildTriggeringPolicy(), triggerPolicy);
    }

    @Test
    public void get_trigger_properties_processor_returns_instance_of_deployment_complete_trigger_properties_processor() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.getTriggerPropertiesProcessor().getClass(), DeploymentProcessChangedTriggerPropertiesProcessor.class);
    }

    @Test
    public void get_edit_parameters_url() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertEquals(sut.getEditParametersUrl(), "resources-path/editOctopusDeploymentProcessChangedTrigger.jsp");
    }

    @Test
    public void is_multiple_triggers_per_build_type_allowed() throws Exception {
        DeploymentProcessChangedBuildTriggerService sut = new DeploymentProcessChangedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry(), new FakeBuildTriggerProperties());
        Assert.assertTrue(sut.isMultipleTriggersPerBuildTypeAllowed());
    }
}

