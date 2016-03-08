package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class HttpContentProviderImplTest {
    final String octopusApiKey = "API-key";
    final Integer timeoutInMilliseconds = 30000;
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    @Test(expectedExceptions = InvalidOctopusUrlException.class, groups = {"needs-real-server"})
    public void get_response_with_octopus_url_with_invalid_host_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl("http://octopus.example.com", octopusApiKey, timeoutInMilliseconds);
        contentProvider.getContent("/api");
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class, groups = {"needs-real-server"})
    public void get_response_with_octopus_url_with_invalid_path_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl + "/not-an-octopus-instance", octopusApiKey, timeoutInMilliseconds);
        contentProvider.getContent("/api");
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class, groups = {"needs-real-server"})
    public void get_response_with_invalid_octopus_api_key_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, "invalid-api-key", timeoutInMilliseconds);
        contentProvider.getContent("/api/projects");
    }

    @Test(expectedExceptions = ProjectNotFoundException.class, groups = {"needs-real-server"})
    public void get_response_with_invalid_project_id_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds);
        contentProvider.getContent("/api/projects/Projects-00");
    }

    @Test(groups = {"needs-real-server"})
    public void get_response_with_from_valid_server() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds);
        String result = contentProvider.getContent("/api");
        Assert.assertTrue(result.contains("\"Application\": \"Octopus Deploy\","));
    }

    public void returns_url_passed_in() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds);
        String url = contentProvider.getUrl();
        Assert.assertEquals(url, realOctopusUrl);
    }
}
