package com.mjrichardson.teamCity.buildTriggers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public interface HttpContentProvider {
    String getOctopusContent(CacheManager.CacheNames cacheName, String uriPath) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException;
    String getContent(CacheManager.CacheNames cacheName, URI uri) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException;

    String getUrl();
}

