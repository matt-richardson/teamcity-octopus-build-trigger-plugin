package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProvider;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakePluginDescriptor;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class UpdateCheckerTest {
    public void check_for_updates_says_update_is_available_when_running_dev_version() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        FakePluginDescriptor pluginDescriptor = new FakePluginDescriptor();
        pluginDescriptor.setPluginVersion("1.0-SNAPSHOT");
        HttpContentProvider httpContentProvider = new FakeContentProvider(null, null);
        UpdateChecker checker = new UpdateChecker(pluginDescriptor, httpContentProvider, new FakeBuildTriggerProperties());
        Assert.assertTrue(checker.isUpdateAvailable());
        Assert.assertEquals(checker.getCurrentVersion(), pluginDescriptor.getPluginVersion());
        Assert.assertEquals(checker.getLatestVersion(), "2.2.0+build.129");
        Assert.assertEquals(checker.getUpdateUrl(), "https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/tag/2.2.0%2Bbuild.129");
    }

    public void check_for_updates_says_update_is_available_when_newer_version_available() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        FakePluginDescriptor pluginDescriptor = new FakePluginDescriptor();
        pluginDescriptor.setPluginVersion("1.0.0");
        HttpContentProvider httpContentProvider = new FakeContentProvider(null, null);
        UpdateChecker checker = new UpdateChecker(pluginDescriptor, httpContentProvider, new FakeBuildTriggerProperties());
        Assert.assertTrue(checker.isUpdateAvailable());
        Assert.assertEquals(checker.getCurrentVersion(), pluginDescriptor.getPluginVersion());
        Assert.assertEquals(checker.getLatestVersion(), "2.2.0+build.129");
        Assert.assertEquals(checker.getUpdateUrl(), "https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/tag/2.2.0%2Bbuild.129");
    }

    public void check_for_updates_says_update_is_not_available_when_no_newer_version_available() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        FakePluginDescriptor pluginDescriptor = new FakePluginDescriptor();
        pluginDescriptor.setPluginVersion("10.0.0");
        HttpContentProvider httpContentProvider = new FakeContentProvider(null, null);
        UpdateChecker checker = new UpdateChecker(pluginDescriptor, httpContentProvider, new FakeBuildTriggerProperties());
        Assert.assertFalse(checker.isUpdateAvailable());
        Assert.assertEquals(checker.getCurrentVersion(), pluginDescriptor.getPluginVersion());
        Assert.assertEquals(checker.getLatestVersion(), "2.2.0+build.129");
        Assert.assertEquals(checker.getUpdateUrl(), "https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/tag/2.2.0%2Bbuild.129");
    }

    public void check_for_updates_says_update_is_not_available_when_exception_happens() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        FakePluginDescriptor pluginDescriptor = new FakePluginDescriptor();
        pluginDescriptor.setPluginVersion("10.0.0");
        HttpContentProvider httpContentProvider = new FakeContentProvider(new IOException("Expected exception"));
        UpdateChecker checker = new UpdateChecker(pluginDescriptor, httpContentProvider, new FakeBuildTriggerProperties());
        Assert.assertFalse(checker.isUpdateAvailable());
    }
}
