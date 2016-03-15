package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeploymentCompleteBuildTriggerServiceTest {

    @Test
    public void get_name_returns_trigger_internal_name() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getName(), "octopusDeploymentCompleteTrigger");
    }

    @Test
    public void get_display_name_returns_user_friendly_name() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getDisplayName(), "Octopus Deployment Complete Trigger");
    }

    @Test
    public void describe_trigger_returns_human_readable_string() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.describeTrigger(new FakeBuildTriggerDescriptor()), "Wait for a new deployment of the-project on server the-server.");
    }

    @Test
    public void get_build_triggering_policy_returns_policy_provided_by_factory() throws Exception {
        FakeBuildTriggeringPolicy triggerPolicy = new FakeBuildTriggeringPolicy();
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(triggerPolicy), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getBuildTriggeringPolicy(), triggerPolicy);
    }

    @Test
    public void get_trigger_properties_processor_returns_instance_of_deployment_complete_trigger_properties_processor() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getTriggerPropertiesProcessor().getClass(), DeploymentCompleteTriggerPropertiesProcessor.class);
    }

    @Test
    public void get_edit_parameters_url() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getEditParametersUrl(), "resources-path/editOctopusDeploymentCompleteTrigger.jsp");
    }

    @Test
    public void is_multiple_triggers_per_build_type_allowed() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertTrue(sut.isMultipleTriggersPerBuildTypeAllowed());
    }
}

