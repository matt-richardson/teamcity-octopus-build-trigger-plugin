package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

@Test
public class DeploymentProcessChangedCheckJobTest {

    @DataProvider(name = "NullAndEmpty")
    public static Object[][] NullAndEmpty() {
        return new Object[][]{{""}, {null}};
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_url_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, value);
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty url) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_apikey_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, value);
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty api key) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_project_id_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, value);
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty project) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    public void perform_returns_an_error_result_if_exception_occurs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProviderThatThrowsException());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertNotNull(result.getGeneralError());
    }

    public void perform_returns_empty_result_if_same_version() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("17");

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());

        //this is when the trigger is created
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored_first_time_then_returns_updates_second_time() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());

        //this is when the trigger is created
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());

        //this is the first check
        FakeDeploymentProcessProvider deploymentProcessProvider = new FakeDeploymentProcessProvider();
        deploymentProcessProvider.setVersion("18");
        DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(deploymentProcessProvider);
        sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());

        result = sut.perform(correlationId);
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        DeploymentProcessChangedSpec updated[] = result.getUpdated().toArray(new DeploymentProcessChangedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Deployment process of the-project-id has changed to version 18 on the-url");
    }

    public void perform_returns_empty_result_if_no_previous_data_stored_and_stores_all_known_environments() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, ParseException, InvalidOctopusUrlException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        String octopusUrl = "the-url";
        properties.put(OCTOPUS_URL, octopusUrl);
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        String octopusProject = "the-project-id";
        properties.put(OCTOPUS_PROJECT_ID, octopusProject);
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        sut.perform(correlationId);
        String key = displayName + "|" + octopusUrl + "|" + octopusProject;

        Assert.assertEquals(dataStorage.getValue(key), "17");
    }

    public void perform_returns_updated_result_if_deployment_process_changed() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("16");

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        CheckResult<DeploymentProcessChangedSpec> result = sut.perform(correlationId);
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        DeploymentProcessChangedSpec updated[] = result.getUpdated().toArray(new DeploymentProcessChangedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Deployment process of the-project-id has changed to version 17 on the-url");
    }

    public void perform_logs_analytics_if_deployment_process_changed() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("16");

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");

        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, analyticsTracker);
        UUID correlationId = UUID.randomUUID();
        sut.perform(correlationId);
        Assert.assertEquals(analyticsTracker.receivedPostCount, 1);
        Assert.assertEquals(analyticsTracker.eventAction, AnalyticsTracker.EventAction.BuildTriggered);
        Assert.assertEquals(analyticsTracker.eventCategory, AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger);
    }

    public void perform_does_not_log_analytics_if_no_changes_detected() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("17");

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, analyticsTracker);
        UUID correlationId = UUID.randomUUID();
        sut.perform(correlationId);
        Assert.assertEquals(analyticsTracker.receivedPostCount, 0);
    }

    public void perform_logs_analytics_if_a_new_trigger_is_added() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, analyticsTracker);

        UUID correlationId = UUID.randomUUID();
        sut.perform(correlationId);
        Assert.assertEquals(analyticsTracker.receivedPostCount, 1);
        Assert.assertEquals(analyticsTracker.eventAction, AnalyticsTracker.EventAction.TriggerAdded);
        Assert.assertEquals(analyticsTracker.eventCategory, AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger);
    }

    public void allow_schedule_returns_false() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //from comments in CheckJob.java from Jetbrains
        // * Current check job may not be allowed to start for some reason,
        // * i.e. build is running that may affect results of quite long check
        // * @return true is this task should not be scheduled just now

        DeploymentProcessProviderFactory DeploymentProcessProviderFactory = new FakeDeploymentProcessProviderFactory(new FakeDeploymentProcessProvider());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("17");

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentProcessChangedCheckJob sut = new DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        Assert.assertFalse(sut.allowSchedule(new FakeBuildTriggerDescriptor()));
    }
}
