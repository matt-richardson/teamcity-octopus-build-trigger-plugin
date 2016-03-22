package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

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
public class MachineAddedCheckJobTest {

    @DataProvider(name = "NullAndEmpty")
    public static Object[][] NullAndEmpty() {
        return new Object[][]{{""}, {null}};
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_url_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithNoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, value);
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty url) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    @Test(dataProvider = "NullAndEmpty")
    public void perform_returns_an_error_result_if_octopus_apikey_is_invalid(String value) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithNoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, value);
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertEquals(result.getGeneralError().getMessage(), "the-display-name settings are invalid (empty api key) in build configuration the-build-type");
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
    }

    public void perform_returns_an_error_result_if_exception_occurs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderThatThrowsExceptions());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        properties.put(OCTOPUS_PROJECT_ID, "the-project-id");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertNotNull(result.getGeneralError());
    }

    public void perform_returns_empty_result_if_no_new_machines_available() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithOneMachine());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Machine("Machine-1", "MachineOne")).toString());

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithTwoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        //this is when the trigger is created
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void perform_returns_empty_result_if_no_previous_data_stored_first_time_then_returns_updates_second_time() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //this situation is when trigger is first setup
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithNoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage();

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        //this is when the trigger is created
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());

        machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithTwoMachines());
        sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        //this is the first check
        result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        MachineAddedSpec updated[] = result.getUpdated().toArray(new MachineAddedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Machine MachineOne added to the-url");
    }

    public void perform_returns_empty_result_if_machine_deleted() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        Machines machines = new Machines();
        machines.add(new Machine("Machine-1", "MachineOne"));
        machines.add(new Machine("Machine-2", "MachineTwo"));//this one is deleted

        CustomDataStorage dataStorage = new FakeCustomDataStorage(machines.toString());

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithOneMachine());
        String displayName = "the-display-name";
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, "the-build-type", dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        //check we are storing the correct data for next time round
        Assert.assertEquals(dataStorage.getValue(displayName + "|" + "the-url"), new Machine("Machine-1", "MachineOne").toString());
    }

    public void perform_returns_updated_result_if_new_machine() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithTwoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Machine("machine-1", "MachineOne")).toString());

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        CheckResult<MachineAddedSpec> result = sut.perform();
        Assert.assertTrue(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertEquals(result.getUpdated().size(), 1);
        MachineAddedSpec updated[] = result.getUpdated().toArray(new MachineAddedSpec[0]);
        Assert.assertEquals(updated[0].getRequestorString(), "Machine MachineTwo added to the-url");
    }

    public void allow_schedule_returns_false() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //from comments in CheckJob.java from Jetbrains
        // * Current check job may not be allowed to start for some reason,
        // * i.e. build is running that may affect results of quite long check
        // * @return true is this task should not be scheduled just now

        MachinesProviderFactory machinesProviderFactory = new FakeMachinesProviderFactory(new FakeMachinesProviderWithNoMachines());
        String displayName = "the-display-name";
        String buildType = "the-build-type";
        CustomDataStorage dataStorage = new FakeCustomDataStorage((new Machine("machine-1", "MachineOne")).toString());

        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_URL, "the-url");
        properties.put(OCTOPUS_APIKEY, "the-api-key");
        MachineAddedCheckJob sut = new MachineAddedCheckJob(machinesProviderFactory, displayName, buildType, dataStorage, properties, new FakeAnalyticsTracker());
        Assert.assertFalse(sut.allowSchedule(new FakeBuildTriggerDescriptor()));
    }
}
