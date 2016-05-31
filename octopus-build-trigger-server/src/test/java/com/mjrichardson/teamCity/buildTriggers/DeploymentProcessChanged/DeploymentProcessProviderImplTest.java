package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentProcessProviderException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Fakes.*;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Test
public class DeploymentProcessProviderImplTest {
    final String octopusUrl = "http://baseUrl";
    final String octopusApiKey = "API-key";
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    static String ProjectWithNoProcess = "Projects-181";
    static String ProjectWithLatestDeploymentSuccessful = "Projects-24";
    static String ProjectThatDoesNotExist = "Projects-00";

    @Test(groups = {"needs-real-server"})
    public void get_deployment_process_version_from_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, new FakeBuildTriggerProperties(), new FakeCacheManager(), new FakeMetricRegistry());
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
        Assert.assertNotNull(newVersion);
    }

    public void get_deployment_process_version() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
        Assert.assertEquals(newVersion, "2");
    }

    public void get_deployment_process_version_for_project_with_no_process() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        UUID correlationId = UUID.randomUUID();
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithNoProcess, correlationId);
        Assert.assertEquals(newVersion, "0");
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void get_deployment_process_version_with_invalid_project() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectThatDoesNotExist, correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployment_process_version_with_octopus_url_with_invalid_host() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployment_process_version_with_octopus_url_with_invalid_path() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
    public void get_deployment_process_version_with_invalid_octopus_api_key() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
    }

    @Test(expectedExceptions = DeploymentProcessProviderException.class)
    public void rethrows_throwable_exceptions_as_deployment_process_provider_exception() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        UUID correlationId = UUID.randomUUID();
        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful, correlationId);
    }
}
