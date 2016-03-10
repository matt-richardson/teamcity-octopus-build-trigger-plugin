package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;

import java.text.ParseException;

public interface MachinesProvider {
    Machines getMachines() throws MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException;
}
