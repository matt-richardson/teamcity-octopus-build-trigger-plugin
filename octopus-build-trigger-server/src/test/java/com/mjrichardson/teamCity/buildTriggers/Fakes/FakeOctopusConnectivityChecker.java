package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class FakeOctopusConnectivityChecker extends OctopusConnectivityChecker {
    private String connectivityCheckResult = null;

    public FakeOctopusConnectivityChecker(String url, String apiKey, Integer connectionTimeout, String connectivityCheckResult) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super(url, apiKey, connectionTimeout, new FakeCacheManager(), new FakeMetricRegistry());
        this.connectivityCheckResult = connectivityCheckResult;
    }

    @Override
    public String checkOctopusConnectivity(UUID correlationId) {
        return this.connectivityCheckResult;
    }
}
