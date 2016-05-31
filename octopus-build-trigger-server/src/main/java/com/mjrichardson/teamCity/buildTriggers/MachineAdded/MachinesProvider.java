package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.MachinesProviderException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;

import java.text.ParseException;
import java.util.UUID;

public interface MachinesProvider {
    Machines getMachines(UUID correlationId) throws MachinesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException;
}
