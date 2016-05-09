package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Test
public class MachinesProviderImplTest {
    final String octopusUrl = "http://baseUrl";
    final String octopusApiKey = "API-key";
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    @Test(groups = {"needs-real-server"})
    public void get_machines_from_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds(), new FakeCacheManager(), new FakeMetricRegistry());
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        Machines newMachines = MachinesProviderImpl.getMachines(correlationId);
        Assert.assertNotNull(newMachines);
    }

    public void get_machines_from_empty_start() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Machines oldMachines = new Machines();
        UUID correlationId = UUID.randomUUID();
        Machines newMachines = MachinesProviderImpl.getMachines(correlationId);
        Assert.assertEquals(newMachines.size(), 1);
        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertNotNull(machine);
        Assert.assertEquals(machine.id, "Machines-1");
        Assert.assertEquals(machine.name, "Octopus Server");
    }

    @Test(enabled = false)
    public void get_machines_from_empty_start_with_no_machines() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        //todo: need to restructure the FakeContentProviderFactory to allow you to override the response
        //      as the test data structure only allows one definition of "machines.json"
        //      then we can enable this test again
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        Machines newMachines = MachinesProviderImpl.getMachines(correlationId);
        Assert.assertEquals(newMachines.size(), 0);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_machines_with_octopus_url_with_invalid_host() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        MachinesProviderImpl.getMachines(correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_machines_with_octopus_url_with_invalid_path() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        MachinesProviderImpl.getMachines(correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
    public void get_machines_with_invalid_octopus_api_key() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        MachinesProviderImpl.getMachines(correlationId);
    }

    @Test(expectedExceptions = MachinesProviderException.class)
    public void rethrows_throwable_exceptions_as_deployment_provider_exception() throws ProjectNotFoundException, MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        MachinesProviderImpl.getMachines(correlationId);
    }

    public void get_machines_when_up_to_date() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        Machines oldMachines = Machines.Parse((new Machine("Machines-1", "Octopus Server", new String[] { "env-id" }, new String[]{ "role-name" })).toString());
        UUID correlationId = UUID.randomUUID();
        Machines newMachines = MachinesProviderImpl.getMachines(correlationId);
        Assert.assertEquals(newMachines.size(), 1);
        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertNotNull(machine);
        Assert.assertEquals(machine.getClass(), NullMachine.class);
    }

    @Test(enabled = false)
    public void get_machines_when_more_than_one_page_of_machines() throws Exception {
        //todo: need to restructure the FakeContentProviderFactory to allow you to override the response
        //      as the test data structure only allows one definition of "machines.json"
        //      then we can enable this test again
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        MachinesProviderImpl MachinesProviderImpl = new MachinesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        Machines oldMachines = Machines.Parse((new Machine("Machines-1", "Octopus Server", new String[] { "env-id" }, new String[]{ "role-name" }).toString()));
        UUID correlationId = UUID.randomUUID();
        Machines newMachines = MachinesProviderImpl.getMachines(correlationId);
        Assert.assertEquals(newMachines.size(), 1);
        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertNotNull(machine);
        Assert.assertEquals(machine.id, "Machines-1");
        Assert.assertEquals(machine.name, "Octopus Server");
    }
}
