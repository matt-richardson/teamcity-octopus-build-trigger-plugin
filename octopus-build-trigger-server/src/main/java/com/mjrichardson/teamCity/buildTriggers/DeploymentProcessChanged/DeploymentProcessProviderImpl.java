package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DeploymentProcessProviderImpl implements DeploymentProcessProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentProcessProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;
    private final AnalyticsTracker analyticsTracker;

    public DeploymentProcessProviderImpl(HttpContentProviderFactory httpContentProviderFactory, AnalyticsTracker analyticsTracker) {
        this.httpContentProviderFactory = httpContentProviderFactory;
        this.analyticsTracker = analyticsTracker;
    }

    public String getDeploymentProcessVersion(String projectId, UUID correlationId) throws ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, DeploymentProcessProviderException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();
            LOG.debug(String.format("%s: Getting deployments from %s for project id '%s'", correlationId, contentProvider.getUrl(), projectId));

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider, correlationId);
            final Project project = getProject(projectId, contentProvider, apiRootResponse, correlationId);
            return getDeploymentProcessVersion(project, contentProvider, correlationId);

        } catch (ProjectNotFoundException | InvalidOctopusApiKeyException | InvalidOctopusUrlException e) {
            throw e;
        } catch (Throwable e) {
            throw new DeploymentProcessProviderException(String.format("Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from %s: %s", url, e), e);
        }
    }

    private String getDeploymentProcessVersion(Project project, HttpContentProvider contentProvider, UUID correlationId) throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException, ParseException {
        String deploymentProcessResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiDeploymentProcess, project.deploymentProcessLink, correlationId);
        ApiDeploymentProcessResponse apiDeploymentProcessResponse = new ApiDeploymentProcessResponse(deploymentProcessResponse);
        return apiDeploymentProcessResponse.version;
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        return new ApiRootResponse(apiResponse, analyticsTracker, correlationId);
    }

    private Project getProject(String projectId, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        String projectsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, apiRootResponse.projectsApiLink, correlationId);
        ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
        Projects projects = apiProjectsResponse.projects;
        while (shouldGetNextProjectsPage(apiProjectsResponse, projects, projectId)) {
            projectsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, apiProjectsResponse.nextLink, correlationId);
            apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            Projects newProjects = apiProjectsResponse.projects;
            projects.add(newProjects);
        }
        return projects.getProject(projectId);
    }

    private boolean shouldGetNextProjectsPage(ApiProjectsResponse apiProjectsResponse, Projects projects, String projectId) {
        if (projects.isEmpty())
            return false;
        if (projects.contains(projectId))
            return false;
        if (apiProjectsResponse.nextLink == null)
            return false;
        return true;
    }
}
