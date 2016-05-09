package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public interface DeploymentsProvider {
    Environments getDeployments(String octopusProject, Environments oldEnvironments, UUID correlationId) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException;
}
