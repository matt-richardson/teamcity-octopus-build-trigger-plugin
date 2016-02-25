package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class OctopusConnectivityChecker {
  private final Logger LOG;
  private HttpContentProvider contentProvider;

  public OctopusConnectivityChecker(String octopusUrl, String apiKey, Integer connectionTimeout, Logger log) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    this(new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout), log);
  }

  OctopusConnectivityChecker(HttpContentProvider contentProvider, Logger log) {
    LOG = log;
    this.contentProvider = contentProvider;
  }

  public String checkOctopusConnectivity() {
    try {
      LOG.info("OctopusConnectivityChecker: checking connectivity to octopus at " + contentProvider.getUrl());
      contentProvider.getContent("/api");

      return null;

    } catch (UnexpectedResponseCodeException e) {
      return e.getMessage();
    } catch (Throwable e) {
      return e.getMessage();
    } finally {
      contentProvider.close();
    }
  }
}
