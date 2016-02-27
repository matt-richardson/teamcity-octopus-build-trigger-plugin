package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.OctopusConnectivityCheckerFactory;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeOctopusConnectivityCheckerFactory extends OctopusConnectivityCheckerFactory {
    private final String connectivityCheckResult;
    private final NoSuchAlgorithmException exception;

    public FakeOctopusConnectivityCheckerFactory(String connectivityCheckResult) {
        this.connectivityCheckResult = connectivityCheckResult;
        this.exception = null;
    }

    public FakeOctopusConnectivityCheckerFactory(NoSuchAlgorithmException exception) {
        this.exception = exception;
        this.connectivityCheckResult = null;
    }

    @Override
    public OctopusConnectivityChecker create(String url, String apiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (this.exception == null)
            return new FakeOctopusConnectivityChecker(url, apiKey, connectionTimeout, connectivityCheckResult);
        throw exception;
    }
}
