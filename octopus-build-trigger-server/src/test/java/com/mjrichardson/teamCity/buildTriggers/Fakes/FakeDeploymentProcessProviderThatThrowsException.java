package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentProcessProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public class FakeDeploymentProcessProviderThatThrowsException implements DeploymentProcessProvider {
    @Override
    public String getDeploymentProcessVersion(String octopusProject, UUID correlationId) throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException, DeploymentProcessProviderException {
        throw new ProjectNotFoundException(octopusProject);
    }
}
