package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machine;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machines;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProviderException;

import java.text.ParseException;

public class FakeMachinesProviderWithTwoMachines implements MachinesProvider {
    @Override
    public Machines getMachines() throws MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Machines machines = new Machines();
        machines.add(new Machine("machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" }));
        machines.add(new Machine("machine-2", "MachineTwo", new String[] { "env-id" }, new String[]{ "role-name" }));
        return machines;
    }
}
