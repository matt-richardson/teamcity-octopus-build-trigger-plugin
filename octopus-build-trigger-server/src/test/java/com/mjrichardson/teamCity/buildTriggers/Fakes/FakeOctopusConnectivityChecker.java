package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeOctopusConnectivityChecker extends OctopusConnectivityChecker{
    private String connectivityCheckResult = null;

    public FakeOctopusConnectivityChecker(String url, String apiKey, Integer connectionTimeout, String connectivityCheckResult) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(url, apiKey, connectionTimeout);
        this.connectivityCheckResult = connectivityCheckResult;
    }

    @Override
    public String checkOctopusConnectivity() {
        return this.connectivityCheckResult;
    }
}
