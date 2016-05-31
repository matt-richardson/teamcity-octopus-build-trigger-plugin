package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public interface DeploymentsProvider {
    Environments getDeployments(String octopusProject, Environments oldEnvironments, UUID correlationId) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException;
}
