package com.mjrichardson.teamCity.buildTriggers;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class OctopusConnectivityCheckerFactory {
    private CacheManager cacheManager;

    public OctopusConnectivityCheckerFactory(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public OctopusConnectivityChecker create(String url, String apiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new OctopusConnectivityChecker(url, apiKey, connectionTimeout, cacheManager);
    }
}
