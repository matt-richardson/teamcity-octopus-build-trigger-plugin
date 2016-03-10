package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machines;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;

import java.text.ParseException;

public class FakeMachinesProviderThatThrowsExceptions extends FakeMachinesProviderWithNoMachines implements MachinesProvider {
    @Override
    public Machines getMachines() throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        throw new ParseException("I tried to parse some stuff, and it didn't work", 0);
    }
}
