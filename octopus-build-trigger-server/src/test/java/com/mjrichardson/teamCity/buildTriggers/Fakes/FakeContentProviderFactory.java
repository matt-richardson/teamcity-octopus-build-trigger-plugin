package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.HttpContentProvider;
import com.mjrichardson.teamCity.buildTriggers.HttpContentProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeContentProviderFactory extends HttpContentProviderFactory {
    private String octopusUrl = null;
    private String octopusApiKey = null;
    private OutOfMemoryError exception = null;

    public FakeContentProviderFactory(String octopusUrl, String octopusApiKey) {
        super(octopusUrl, octopusApiKey, 100);
        this.octopusUrl = octopusUrl;
        this.octopusApiKey = octopusApiKey;
    }

    public FakeContentProviderFactory(OutOfMemoryError exception) {
        super("", "", 100);
        this.exception = exception;
    }

    @Override
    public HttpContentProvider getContentProvider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (exception != null)
            return new FakeContentProvider(exception);
        return new FakeContentProvider(octopusUrl, octopusApiKey);
    }
}
