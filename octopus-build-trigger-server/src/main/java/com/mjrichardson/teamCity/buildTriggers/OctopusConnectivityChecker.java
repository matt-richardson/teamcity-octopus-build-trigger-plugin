package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class OctopusConnectivityChecker {
    private static final Logger LOG = Logger.getInstance(OctopusConnectivityChecker.class.getName());
    private HttpContentProvider contentProvider;

    public OctopusConnectivityChecker(String octopusUrl, String apiKey, Integer connectionTimeout, CacheManager cacheManager, MetricRegistry metricRegistry) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout, cacheManager, metricRegistry));
    }

    OctopusConnectivityChecker(HttpContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public String checkOctopusConnectivity(UUID correlationId) {
        try {
            LOG.info(String.format("%s: checking connectivity to octopus at %s", correlationId, contentProvider.getUrl()));
            contentProvider.getOctopusContent(CacheManager.CacheNames.NoCache, "/api", correlationId);

            return null;

        } catch (UnexpectedResponseCodeException e) {
            return e.getMessage();
        } catch (Throwable e) {
            return e.getMessage();
        }
    }
}
