package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environment;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environments;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.UUID;

public class FakeDeploymentsProviderWithOneDeployment implements DeploymentsProvider {
    public FakeDeploymentsProviderWithOneDeployment() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    @Override
    public Environments getDeployments(String octopusProject, Environments oldEnvironments, UUID correlationId) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Environment environment = new Environment("Environments-1", new OctopusDate(2016, 2, 25), new OctopusDate(2016, 2, 25), "the-release-id", "the-deployment-id", "the-version", "the-project-id");
        return new Environments(environment);
    }
}
