package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAsyncBuildTriggerFactory;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerDescriptor;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggeringPolicy;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakePluginDescriptor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeploymentCompleteBuildTriggerServiceTest {

    @Test
    public void get_name_returns_trigger_internal_name() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getName(), "octopusDeploymentCompleteTrigger");
    }

    @Test
    public void get_display_name_returns_user_friendly_name() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getDisplayName(), "Octopus Deployment Complete Trigger");
    }

    @Test
    public void describe_trigger_returns_human_readable_string() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.describeTrigger(new FakeBuildTriggerDescriptor()), "Wait for a new deployment of the-project on server the-server.");
    }

    @Test
    public void get_build_triggering_policy_returns_policy_provided_by_factory() throws Exception {
        FakeBuildTriggeringPolicy triggerPolicy = new FakeBuildTriggeringPolicy();
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(triggerPolicy));
        Assert.assertEquals(sut.getBuildTriggeringPolicy(), triggerPolicy);
    }

    @Test
    public void get_trigger_properties_processor_returns_instance_of_deployment_complete_trigger_properties_processor() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getTriggerPropertiesProcessor().getClass(), DeploymentCompleteTriggerPropertiesProcessor.class);
    }

    @Test
    public void testGetEditParametersUrl() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getEditParametersUrl(), "resources-path/editOctopusDeploymentCompleteTrigger.jsp");
    }

    @Test
    public void testIsMultipleTriggersPerBuildTypeAllowed() throws Exception {
        DeploymentCompleteBuildTriggerService sut = new DeploymentCompleteBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertTrue(sut.isMultipleTriggersPerBuildTypeAllowed());
    }
}

