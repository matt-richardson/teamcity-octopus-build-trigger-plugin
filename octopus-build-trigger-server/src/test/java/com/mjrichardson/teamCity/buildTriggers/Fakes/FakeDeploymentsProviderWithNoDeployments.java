package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environments;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProvider;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProviderException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class FakeDeploymentsProviderWithNoDeployments implements DeploymentsProvider {
    public FakeDeploymentsProviderWithNoDeployments() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    @Override
    public Environments getDeployments(String octopusProject, Environments oldEnvironments) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        return new Environments();
    }
}
