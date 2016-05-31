package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machine;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machines;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.MachinesProviderException;

import java.text.ParseException;
import java.util.UUID;

public class FakeMachinesProviderWithTwoMachines implements MachinesProvider {
    @Override
    public Machines getMachines(UUID correlationId) throws MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Machines machines = new Machines();
        machines.add(new Machine("machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" }));
        machines.add(new Machine("machine-2", "MachineTwo", new String[] { "env-id" }, new String[]{ "role-name" }));
        return machines;
    }
}
