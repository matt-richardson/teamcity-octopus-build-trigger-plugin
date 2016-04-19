package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProvider;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class OctopusConnectivityCheckerTest {
    public void check_octopus_connectivity_returns_null_when_content_provider_returns_content() {
        FakeContentProvider contentProvider = new FakeContentProvider("http://fake-url", "API-KEY");
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), null);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_invalid_octopus_api_key_excepton() {
        Exception exception = new InvalidOctopusApiKeyException(403, "Invalid api key");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_invalid_octopus_url_exception() throws URISyntaxException {
        Exception exception = new InvalidOctopusUrlException(new URI("http://example.org"));
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_unexpected_response_code_exception() throws URISyntaxException {
        Exception exception = new UnexpectedResponseCodeException(451, "unexpected response");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_project_not_found_exception() throws URISyntaxException {
        Exception exception = new ProjectNotFoundException("Projects-61");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_throwable() throws URISyntaxException {
        Throwable exception = new OutOfMemoryError();
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    @Test(groups = {"needs-real-server"})
    public void check_octopus_connectivity_against_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final String realOctopusUrl = "http://windows10vm.local/";
        final Integer timeoutInMilliseconds = 30000;

        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(realOctopusUrl, "", timeoutInMilliseconds, new FakeCacheManager(), new FakeMetricRegistry());
        String result = sut.checkOctopusConnectivity();
        Assert.assertEquals(result, null);
    }
}
