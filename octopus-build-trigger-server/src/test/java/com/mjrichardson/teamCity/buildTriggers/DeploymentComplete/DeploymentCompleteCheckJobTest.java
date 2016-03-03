package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

@Test
public class DeploymentCompleteCheckJobTest {

    @DataProvider(name = "NullAndEmpty")
    public static Object[][] NullAndEmpty() {
        return new Object[][] {{""}, {null}};
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_url_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithNoDeployments());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, value);
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty url) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_apikey_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithNoDeployments());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, value);
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty api key) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_project_id_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithNoDeployments());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, value);
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty project) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    public void perform_returns_an_error_result_if_exception_occurs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderThatThrowsExceptions());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertNotNull(result.getGeneralError());
    }

    public void perform_returns_empty_result_if_no_new_deployments_available() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithOneDeployment());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("Environments-1;2016-02-25T00:00:00.000+00:00;2016-02-25T00:00:00.000+00:00;");

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    //todo: issue where if trigger is created before any deployments, then it will miss the first deployment
    public void perform_returns_empty_result_if_no_previous_data_stored() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithOneDeployment());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_new_deployment_but_it_failed_when_only_triggering_on_successful_builds() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithOneFailedDeployment());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("Environments-1;2016-02-01T00:00:00.000+00:00;2016-02-01T00:00:00.000+00:00;");

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_updated_result_if_new_deployment_but_it_failed_when_triggering_on_all_builds() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithOneFailedDeployment());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("Environments-1;2016-02-01T00:00:00.000+00:00;2016-02-01T00:00:00.000+00:00;");

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "false");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        DeploymentCompleteSpec updated[] = result.getUpdated().toArray(new DeploymentCompleteSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Deployment of the-project-id to Environments-1 on the-url");
    }

    public void perform_returns_updated_result_if_new_deployment() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithOneDeployment());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("Environments-1;2016-02-01T00:00:00.000+00:00;2016-02-01T00:00:00.000+00:00;");

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<DeploymentCompleteSpec>result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        DeploymentCompleteSpec updated[] = result.getUpdated().toArray(new DeploymentCompleteSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Successful deployment of the-project-id to Environments-1 on the-url");
    }

    public void allow_schedule_returns_false() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //from comments in CheckJob.java from Jetbrains
        // * Current check job may not be allowed to start for some reason,
        // * i.e. build is running that may affect results of quite long check
        // * @return true is this task should not be scheduled just now

        DeploymentsProviderFactory deploymentsProviderFactory = new FakeDeploymentsProviderFactory(new FakeDeploymentsProviderWithNoDeployments());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage("Environments-1;2016-02-01T00:00:00.000+00:00;2016-02-01T00:00:00.000+00:00;");

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");
        DeploymentCompleteCheckJob sut = new DeploymentCompleteCheckJob(deploymentsProviderFactory, displayName, buildType, dataStorage, properties);
        Assert.assertFalse(sut.allowSchedule(new FakeBuildTriggerDescriptor()));
    }
}
