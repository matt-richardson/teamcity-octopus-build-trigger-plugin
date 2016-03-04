package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
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
public class ReleaseCreatedCheckJobTest {

    @DataProvider(name = "NullAndEmpty")
    public static Object[][] NullAndEmpty() {
        return new Object[][] {{""}, {null}};
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_url_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithNoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, value);
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty url) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_apikey_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithNoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, value);
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty api key) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_project_id_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithNoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, value);
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty project) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    public void perform_returns_an_error_result_if_exception_occurs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderThatThrowsExceptions());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertNotNull(result.getGeneralError());
    }

    public void perform_returns_empty_result_if_no_new_releases_available() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithOneRelease());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Release("release-1", new OctopusDate(2016,3,1), "1.0.0")).toString());

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithTwoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        //this is when the trigger is created
        CheckResult<ReleaseCreatedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored_first_time_then_returns_updates_second_time() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithNoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        //this is when the trigger is created
        CheckResult<ReleaseCreatedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());

        releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithTwoReleases());
        sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        //this is the first check
        result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        ReleaseCreatedSpec updated[] = result.getUpdated().toArray(new ReleaseCreatedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Release 1.0.0 of project the-project-id created on the-url");
    }

    public void perform_returns_updated_result_if_new_release() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithTwoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Release("release-1", new OctopusDate(2016,3,1), "1.0.0")).toString());

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        CheckResult<ReleaseCreatedSpec>result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        ReleaseCreatedSpec updated[] = result.getUpdated().toArray(new ReleaseCreatedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Release 1.1.0 of project the-project-id created on the-url");
    }

    public void allow_schedule_returns_false() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //from comments in CheckJob.java from Jetbrains
        // * Current check job may not be allowed to start for some reason,
        // * i.e. build is running that may affect results of quite long check
        // * @return true is this task should not be scheduled just now

        ReleasesProviderFactory releasesProviderFactory = new FakeReleasesProviderFactory(new FakeReleasesProviderWithNoReleases());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Release("release-1", new OctopusDate(2016,3,1), "1.0.0")).toString());

        Map<String,String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        properties.put(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT, "true");
        ReleaseCreatedCheckJob sut = new ReleaseCreatedCheckJob(releasesProviderFactory, displayName, buildType, dataStorage, properties);
        Assert.assertFalse(sut.allowSchedule(new FakeBuildTriggerDescriptor()));
    }
}
