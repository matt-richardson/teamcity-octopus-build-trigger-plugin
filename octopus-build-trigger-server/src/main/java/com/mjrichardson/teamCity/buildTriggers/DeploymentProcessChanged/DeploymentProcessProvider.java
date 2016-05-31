package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentProcessProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public interface DeploymentProcessProvider {
    String getDeploymentProcessVersion(String octopusProject, UUID correlationId) throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException, DeploymentProcessProviderException;
}
