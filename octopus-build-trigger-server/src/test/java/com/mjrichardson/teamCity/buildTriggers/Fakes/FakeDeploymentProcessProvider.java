package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessProvider;
import com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged.DeploymentProcessProviderException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public class FakeDeploymentProcessProvider implements DeploymentProcessProvider {
    private String version = "17";

    @Override
    public String getDeploymentProcessVersion(String octopusProject, UUID correlationId) throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException, DeploymentProcessProviderException {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

