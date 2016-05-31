package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidCacheConfigurationException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.UnexpectedResponseCodeException;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Test
public class HttpContentProviderImplTest {
    final String octopusApiKey = "API-key";
    final Integer timeoutInMilliseconds = 30000;
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    @Test(expectedExceptions = InvalidOctopusUrlException.class, groups = {"needs-real-server"})
    public void get_response_from_real_server_with_octopus_url_with_invalid_host_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException, InvalidCacheConfigurationException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl("http://octopus.example.com", octopusApiKey, timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        UUID correlationId = UUID.randomUUID();
        contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class, groups = {"needs-real-server"})
    public void get_response_from_real_server_with_octopus_url_with_invalid_path_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException, InvalidCacheConfigurationException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl + "/not-an-octopus-instance", octopusApiKey, timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        UUID correlationId = UUID.randomUUID();
        contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class, groups = {"needs-real-server"})
    public void get_response_from_real_server_with_invalid_octopus_api_key_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException, InvalidCacheConfigurationException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, "invalid-api-key", timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        UUID correlationId = UUID.randomUUID();
        contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, "/api/projects", correlationId);
    }

    @Test(expectedExceptions = ProjectNotFoundException.class, groups = {"needs-real-server"})
    public void get_response_from_real_server_with_invalid_project_id_throws_exception() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException, InvalidCacheConfigurationException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        UUID correlationId = UUID.randomUUID();
        contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, "/api/projects/Projects-00", correlationId);
    }

    @Test(groups = {"needs-real-server"})
    public void get_response_from_real_server_with_from_valid_server() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException, InvalidCacheConfigurationException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        UUID correlationId = UUID.randomUUID();
        String result = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        Assert.assertTrue(result.contains("\"Application\": \"Octopus Deploy\","));
    }

    public void returns_url_passed_in() throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnexpectedResponseCodeException, IOException, URISyntaxException {
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        String url = contentProvider.getUrl();
        Assert.assertEquals(url, realOctopusUrl);
    }

    @Test(groups = {"needs-real-server"})
    public void saves_response_in_cache() throws IOException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, UnexpectedResponseCodeException, KeyStoreException, InvalidOctopusUrlException, KeyManagementException, InvalidOctopusApiKeyException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new FakeCacheManager();
        final URI uri = new URL(realOctopusUrl + "/api").toURI();
        UUID correlationId = UUID.randomUUID();
        String originalValue = cacheManager.getFromCache(CacheManager.CacheNames.ApiRoot, uri, correlationId);
        Assert.assertEquals(originalValue, null);
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds, cacheManager, new FakeMetricRegistry());
        String result = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        String newValue = cacheManager.getFromCache(CacheManager.CacheNames.ApiRoot, uri, correlationId);
        Assert.assertEquals(newValue, result);
    }

    @Test(groups = {"needs-real-server"})
    public void uses_cached_response() throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new FakeCacheManager();
        final URI uri = new URL(realOctopusUrl + "/api").toURI();
        UUID correlationId = UUID.randomUUID();
        cacheManager.addToCache(CacheManager.CacheNames.ApiRoot, uri, "cached value", correlationId);
        HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, timeoutInMilliseconds, cacheManager, new FakeMetricRegistry());
        String result = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        Assert.assertEquals(result, "cached value");
    }
}
