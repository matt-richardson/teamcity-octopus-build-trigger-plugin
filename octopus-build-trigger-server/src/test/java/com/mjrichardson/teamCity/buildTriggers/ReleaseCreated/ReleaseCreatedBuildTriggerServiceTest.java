package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReleaseCreatedBuildTriggerServiceTest {

    @Test
    public void get_name_returns_trigger_internal_name() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getName(), "octopusReleaseCreatedTrigger");
    }

    @Test
    public void get_display_name_returns_user_friendly_name() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getDisplayName(), "Octopus Release Created Trigger");
    }

    @Test
    public void describe_trigger_returns_human_readable_string() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.describeTrigger(new FakeBuildTriggerDescriptor()), "Wait for a new release of the-project to be created on server the-server.");
    }

    @Test
    public void get_build_triggering_policy_returns_policy_provided_by_factory() throws Exception {
        FakeBuildTriggeringPolicy triggerPolicy = new FakeBuildTriggeringPolicy();
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(triggerPolicy), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getBuildTriggeringPolicy(), triggerPolicy);
    }

    @Test
    public void get_trigger_properties_processor_returns_instance_of_deployment_complete_trigger_properties_processor() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getTriggerPropertiesProcessor().getClass(), ReleaseCreatedTriggerPropertiesProcessor.class);
    }

    @Test
    public void get_edit_parameters_url() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertEquals(sut.getEditParametersUrl(), "resources-path/editOctopusReleaseCreatedTrigger.jsp");
    }

    @Test
    public void is_multiple_triggers_per_build_type_allowed() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(), new FakeAnalyticsTracker());
        Assert.assertTrue(sut.isMultipleTriggersPerBuildTypeAllowed());
    }
}

