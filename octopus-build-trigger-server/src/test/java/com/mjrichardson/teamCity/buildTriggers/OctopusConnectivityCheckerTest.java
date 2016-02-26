package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class OctopusConnectivityCheckerTest {
    private static final Logger LOG = Logger.getInstance(OctopusConnectivityChecker.class.getName());

    public void check_octopus_connectivity_returns_null_when_content_provider_returns_content() {
        FakeContentProvider contentProvider = new FakeContentProvider();
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), null);
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_invalid_octopus_api_key_excepton() {
        Exception exception = new InvalidOctopusApiKeyException(403, "Invalid api key");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_invalid_octopus_url_exception() throws URISyntaxException {
        Exception exception = new InvalidOctopusUrlException(new URI("http://example.org"));
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_unexpected_response_code_exception() throws URISyntaxException {
        Exception exception = new UnexpectedResponseCodeException(451, "unexpected response");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_gets_project_not_found_exception() throws URISyntaxException {
        Exception exception = new ProjectNotFoundException("Projects-61");
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    public void check_octopus_connectivity_returns_error_message_when_throwable() throws URISyntaxException {
        Throwable exception = new OutOfMemoryError();
        FakeContentProvider contentProvider = new FakeContentProvider(exception);
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker(contentProvider);
        Assert.assertEquals(sut.checkOctopusConnectivity(), exception.getMessage());
        Assert.assertTrue(contentProvider.closeWasCalled);
        Assert.assertEquals(contentProvider.requestedUriPath, "/api");
    }

    @Test(groups = { "needs-internet-access" })
    public void check_octopus_connectivity_against_live_octopus_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        OctopusConnectivityChecker sut = new OctopusConnectivityChecker("https://demo.octopusdeploy.com", "", 6000);
        String result = sut.checkOctopusConnectivity();
        Assert.assertEquals(result, null);
    }

    private class FakeContentProvider implements HttpContentProvider {
        private final Throwable exception;
        public String requestedUriPath;
        public boolean closeWasCalled;

        public FakeContentProvider() {
            this(null);
        }

        public FakeContentProvider(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public void close() {
            closeWasCalled = true;
        }

        @Override
        public String getContent(String uriPath) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException {
            requestedUriPath = uriPath;
            if (this.exception != null) {
                //there must be a better way of doing this
                if (exception.getClass() == IOException.class)
                    throw (IOException)exception;
                if (exception.getClass() == UnexpectedResponseCodeException.class)
                    throw (UnexpectedResponseCodeException)exception;
                if (exception.getClass() == InvalidOctopusApiKeyException.class)
                    throw (InvalidOctopusApiKeyException)exception;
                if (exception.getClass() == InvalidOctopusUrlException.class)
                    throw (InvalidOctopusUrlException)exception;
                if (exception.getClass() == URISyntaxException.class)
                    throw (URISyntaxException)exception;
                if (exception.getClass() == ProjectNotFoundException.class)
                    throw (ProjectNotFoundException)exception;
            }
            return "some content";
        }

        @Override
        public String getUrl() {
            return null;
        }
    }
}
