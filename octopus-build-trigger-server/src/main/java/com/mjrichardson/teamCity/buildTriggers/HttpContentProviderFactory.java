package com.mjrichardson.teamCity.buildTriggers;

import org.jetbrains.annotations.NotNull;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HttpContentProviderFactory {

    @NotNull
    private final String octopusUrl;
    @NotNull
    private final String apiKey;
    @NotNull
    private final Integer connectionTimeout;

    public HttpContentProviderFactory(@NotNull String octopusUrl, @NotNull String apiKey, @NotNull Integer connectionTimeout) {
        this.octopusUrl = octopusUrl;
        this.apiKey = apiKey;
        this.connectionTimeout = connectionTimeout;
    }

    public HttpContentProvider getContentProvider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout);
    }
}
