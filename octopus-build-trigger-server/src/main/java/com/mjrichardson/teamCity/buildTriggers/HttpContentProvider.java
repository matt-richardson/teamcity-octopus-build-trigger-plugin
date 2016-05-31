package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidCacheConfigurationException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.UnexpectedResponseCodeException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public interface HttpContentProvider {
    String getOctopusContent(CacheManager.CacheNames cacheName, String uriPath, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException;
    String getContent(CacheManager.CacheNames cacheName, URI uri, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException;

    String getUrl();
}

