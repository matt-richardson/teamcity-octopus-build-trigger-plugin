package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidCacheConfigurationException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.UnexpectedResponseCodeException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

//todo:check against multiple versions of Octopus
public class FakeContentProvider implements HttpContentProvider {
    private Throwable exception = null;
    public String requestedUriPath = null;
    private String octopusUrl = null;
    private String octopusApiKey = null;

    public FakeContentProvider(String octopusUrl, String octopusApiKey) {
        this.octopusUrl = octopusUrl;
        this.octopusApiKey = octopusApiKey;
    }

    public FakeContentProvider(Throwable exception) {
        this.exception = exception;
        this.octopusUrl = "http://fake-url";
    }

    @Override
    public String getOctopusContent(CacheManager.CacheNames cacheName, String uriPath, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException {
        requestedUriPath = uriPath;
        if (this.exception != null) {
            //there must be a better way of doing this
            if (exception.getClass() == IOException.class)
                throw (IOException) exception;
            if (exception.getClass() == UnexpectedResponseCodeException.class)
                throw (UnexpectedResponseCodeException) exception;
            if (exception.getClass() == InvalidOctopusApiKeyException.class)
                throw (InvalidOctopusApiKeyException) exception;
            if (exception.getClass() == InvalidOctopusUrlException.class)
                throw (InvalidOctopusUrlException) exception;
            if (exception.getClass() == URISyntaxException.class)
                throw (URISyntaxException) exception;
            if (exception.getClass() == ProjectNotFoundException.class)
                throw (ProjectNotFoundException) exception;
            if (exception.getClass() == OutOfMemoryError.class)
                throw (OutOfMemoryError) exception;
        }

        String s = octopusUrl + uriPath;
        if (this.octopusUrl.contains("not-an-octopus-instance") || this.octopusUrl.contains("example.com")) {
            throw new InvalidOctopusUrlException(new URI(s)); //this is a bit odd, but we are just checking to make sure the right exception gets back to the right spot
        }
        if (!this.octopusApiKey.startsWith("API-")) {
            throw new InvalidOctopusApiKeyException(401, "Invalid octopus api key");
        }
        if (uriPath.endsWith("/api/projects/Projects-00")) {
            throw new ProjectNotFoundException("Projects-00");
        }

        final String resourceName = "/responses/3.3.0/" + s.replace(octopusUrl + "/", "").replace("?", "/") + ".json";
        try {
            InputStream resource = getClass().getResourceAsStream(resourceName);
            return IOUtils.toString(resource);
        } catch (NullPointerException | IOException e) {
            throw new InvalidOctopusUrlException(new URI(s), e);
        }
    }

    @Override
    public String getContent(CacheManager.CacheNames cacheName, URI uri, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        if (this.exception != null) {
            //there must be a better way of doing this
            if (exception.getClass() == IOException.class)
                throw (IOException) exception;
        }
        final String resourceName = "/responses/" + uri.getHost() + uri.getPath().replace("?", "/") + ".json";
        InputStream resource = getClass().getResourceAsStream(resourceName);
        return IOUtils.toString(resource);
    }

    @Override
    public String getUrl() {
        return octopusUrl;
    }
}
