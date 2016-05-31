package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environments;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.UUID;

public class FakeDeploymentsProviderWithNoDeployments implements DeploymentsProvider {
    public FakeDeploymentsProviderWithNoDeployments() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    @Override
    public Environments getDeployments(String octopusProject, Environments oldEnvironments, UUID correlationId) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        return new Environments();
    }
}
