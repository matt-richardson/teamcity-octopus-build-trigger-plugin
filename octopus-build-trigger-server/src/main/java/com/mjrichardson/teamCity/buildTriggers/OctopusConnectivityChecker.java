package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class OctopusConnectivityChecker {
    private static final Logger LOG = Logger.getInstance(OctopusConnectivityChecker.class.getName());
    private HttpContentProvider contentProvider;

    public OctopusConnectivityChecker(String octopusUrl, String apiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout));
    }

    OctopusConnectivityChecker(HttpContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public String checkOctopusConnectivity() {
        try {
            LOG.info("checking connectivity to octopus at " + contentProvider.getUrl());
            contentProvider.getContent("/api");

            return null;

        } catch (UnexpectedResponseCodeException e) {
            return e.getMessage();
        } catch (Throwable e) {
            return e.getMessage();
        }
    }
}
