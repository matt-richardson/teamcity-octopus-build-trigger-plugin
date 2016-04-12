package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machine;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machines;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProviderException;

import java.text.ParseException;

public class FakeMachinesProviderWithOneMachine implements MachinesProvider {
    @Override
    public Machines getMachines() throws MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Machine machine = new Machine("Machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" });
        Machines machines = new Machines();
        machines.add(machine);
        return machines;
    }
}
