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
        return new Machines(new Machine("Machine-1", "MachineOne"));
    }
}
