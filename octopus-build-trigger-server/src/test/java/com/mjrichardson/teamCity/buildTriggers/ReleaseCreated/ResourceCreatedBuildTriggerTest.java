package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAsyncBuildTriggerFactory;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerDescriptor;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggeringPolicy;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakePluginDescriptor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResourceCreatedBuildTriggerTest {

    @Test
    public void get_name_returns_trigger_internal_name() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getName(), "octopusReleaseCreatedTrigger");
    }

    @Test
    public void get_display_name_returns_user_friendly_name() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getDisplayName(), "Octopus Release Created Trigger");
    }

    @Test
    public void describe_trigger_returns_human_readable_string() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.describeTrigger(new FakeBuildTriggerDescriptor()), "Wait for a new release of the-project to be created on server the-server.");
    }

    @Test
    public void get_build_triggering_policy_returns_policy_provided_by_factory() throws Exception {
        FakeBuildTriggeringPolicy triggerPolicy = new FakeBuildTriggeringPolicy();
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory(triggerPolicy));
        Assert.assertEquals(sut.getBuildTriggeringPolicy(), triggerPolicy);
    }

    @Test
    public void get_trigger_properties_processor_returns_instance_of_deployment_complete_trigger_properties_processor() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getTriggerPropertiesProcessor().getClass(), ReleaseCreatedTriggerPropertiesProcessor.class);
    }

    @Test
    public void testGetEditParametersUrl() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertEquals(sut.getEditParametersUrl(), "resources-path/editOctopusReleaseCreatedTrigger.jsp");
    }

    @Test
    public void testIsMultipleTriggersPerBuildTypeAllowed() throws Exception {
        ReleaseCreatedBuildTriggerService sut = new ReleaseCreatedBuildTriggerService(new FakePluginDescriptor(), new FakeAsyncBuildTriggerFactory());
        Assert.assertTrue(sut.isMultipleTriggersPerBuildTypeAllowed());
    }
}
