package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.*;
import com.mjrichardson.teamCity.buildTriggers.Model.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DeploymentsProviderImpl implements DeploymentsProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentsProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;
    private final AnalyticsTracker analyticsTracker;

    public DeploymentsProviderImpl(HttpContentProviderFactory httpContentProviderFactory, AnalyticsTracker analyticsTracker) {
        this.httpContentProviderFactory = httpContentProviderFactory;
        this.analyticsTracker = analyticsTracker;
    }

    public Environments getDeployments(String projectId, Environments oldEnvironments, UUID correlationId) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();
            LOG.debug(String.format("%s: Getting deployments from %s for project id '%s'", correlationId, contentProvider.getUrl(), projectId));

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider, correlationId);
            final Project project = getProject(projectId, contentProvider, apiRootResponse, correlationId);
            return getDeployments(projectId, oldEnvironments, contentProvider, apiRootResponse, project, correlationId);

        } catch (ProjectNotFoundException | InvalidOctopusApiKeyException | InvalidOctopusUrlException e) {
            throw e;
        } catch (Throwable e) {
            throw new DeploymentsProviderException(String.format("Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from %s: %s", url, e), e);
        }
    }

    private Environments getDeployments(String projectId, Environments oldEnvironments, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, Project project, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, java.text.ParseException, ParseException, InvalidCacheConfigurationException {
        final String progressionResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProgression, project.progressionApiLink, correlationId);
        final ApiProgressionResponse apiProgressionResponse = new ApiProgressionResponse(progressionResponse, correlationId);

        if (apiProgressionResponse.haveCompleteInformation)
            return apiProgressionResponse.environments;

        analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, AnalyticsTracker.EventAction.FallingBackToDeploymentsApi, correlationId);
        Environments environmentsFromDeploymentsApi = getEnvironmentsFromApi(projectId, oldEnvironments, contentProvider, apiRootResponse, apiProgressionResponse, correlationId);

        AnalyticsTracker.EventAction result = determineOutcomeOfFallback(apiProgressionResponse.environments, environmentsFromDeploymentsApi, correlationId);
        analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, result, correlationId);

        //try and return the most useful response
        if ((result == AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedBetterInformation) ||
                (result == AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedMoreEnvironments)) {
            return environmentsFromDeploymentsApi;
        }

        return apiProgressionResponse.environments;
    }

    AnalyticsTracker.EventAction determineOutcomeOfFallback(Environments environmentsFromProgressionApi, Environments environmentsFromDeploymentsApi, UUID correlationId) {
        if (environmentsFromProgressionApi.size() < environmentsFromDeploymentsApi.size()) {
            LOG.info(String.format("%s: Got %d environments from deployments api, but %d environments from progression api.",
                    correlationId, environmentsFromDeploymentsApi.size(), environmentsFromProgressionApi.size()));
            LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
            LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
            return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedMoreEnvironments;
        }
        else if (environmentsFromProgressionApi.size() > environmentsFromDeploymentsApi.size()) {
            LOG.info(String.format("%s: Got %d environments from deployments api, but %d environments from progression api.",
                    correlationId, environmentsFromDeploymentsApi.size(), environmentsFromProgressionApi.size()));
            LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
            LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
            return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedFewerEnvironments;
        }
        else if (environmentsFromProgressionApi.equals(environmentsFromDeploymentsApi)) {
            LOG.info(String.format("%s: Fallback to deployments api produced same results", correlationId));
            return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedSameResults;
        }
        else
        {
            for (Environment environmentFromProgressionApi : environmentsFromProgressionApi) {
                Environment environmentFromDeploymentApi = environmentsFromDeploymentsApi.getEnvironment(environmentFromProgressionApi.environmentId);
                if (environmentFromDeploymentApi.equals(environmentFromProgressionApi)) {
                    LOG.info(String.format("%s: Environment %s from deployments api is same as the environment from the progression api",
                            correlationId, environmentFromDeploymentApi.environmentId));
                }
                else if (environmentFromDeploymentApi.getClass() == NullEnvironment.class) {
                    LOG.info(String.format("%s: Got different environments from deployments api and progression api.", correlationId));
                    LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
                    LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
                    return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedDifferentEnvironments;
                }
                else if (environmentFromProgressionApi.isLatestSuccessfulDeploymentOlderThen(environmentFromDeploymentApi.latestSuccessfulDeployment)) {
                    LOG.info(String.format("%s: Environment %s from deployments api has a newer latestSuccessfulDeployment date (%s) than the environment from the progression api (%s)",
                            correlationId, environmentFromDeploymentApi.environmentId, environmentFromDeploymentApi.latestSuccessfulDeployment, environmentFromProgressionApi.latestSuccessfulDeployment));
                    LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
                    LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
                    return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedBetterInformation;
                }
                else if (environmentFromProgressionApi.isLatestDeploymentOlderThan(environmentFromDeploymentApi.latestDeployment)) {
                    LOG.info(String.format("%s: Environment %s from deployments api has a newer latestDeployment date (%s) than the environment from the progression api (%s)",
                            correlationId, environmentFromDeploymentApi.environmentId, environmentFromDeploymentApi.latestDeployment, environmentFromProgressionApi.latestDeployment));
                    LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
                    LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
                    return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedBetterInformation;
                }
                else {
                    LOG.info(String.format("%s: Environment %s from deployments api (%s) is worse than the environment from the progression api (%s)",
                            correlationId, environmentFromDeploymentApi.environmentId, environmentFromDeploymentApi.toString(), environmentFromProgressionApi.toString()));
                    LOG.debug(String.format("%s: Environments from progression api: %s", correlationId, environmentsFromProgressionApi.toString()));
                    LOG.debug(String.format("%s: Environments from deployments api: %s", correlationId, environmentsFromDeploymentsApi.toString()));
                    return AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedWorseResults;
                }
            }
            LOG.warn(String.format("%s: Not expecting to get to this circumstance!", correlationId));
            assert false;
        }
        return AnalyticsTracker.EventAction.FallBackStatusUnknown;
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

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        return new ApiRootResponse(apiResponse, analyticsTracker, correlationId);
    }

    @NotNull
    private Environments getEnvironmentsFromApi(String projectId, Environments oldEnvironments, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, ApiProgressionResponse apiProgressionResponse, UUID correlationId) throws URISyntaxException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, IOException, ParseException, java.text.ParseException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        String deploymentsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiDeployments, apiRootResponse.deploymentsApiLink + "?Projects=" + projectId, correlationId);
        ApiDeploymentsResponse response = new ApiDeploymentsResponse(deploymentsResponse);
        Environments result = new Environments();

        //we trust that the progression response is going to return the current environments
        LOG.debug(String.format("%s: Setting up initial state based on oldEnvironments (populated from stored data)", correlationId));
        for (Environment environment : apiProgressionResponse.environments) {
            Environment lastKnownEnvironmentState = oldEnvironments.getEnvironment(environment.environmentId);
            if (lastKnownEnvironmentState.getClass() == NullEnvironment.class) {
                LOG.debug(String.format("%s: Adding empty environment '%s' (no known deployments to that environment)", correlationId, environment.environmentId));
                result.addEnvironment(environment.environmentId);
            } else {
                LOG.debug(String.format("%s: Adding lastKnownEnvironmentState: '%s'", correlationId, lastKnownEnvironmentState.toString()));
                result.addOrUpdate(lastKnownEnvironmentState);
            }
        }

        for (Deployment item : response.deployments) {
            if (ProcessDeployment(contentProvider, oldEnvironments, result, item, correlationId))
                return result;
        }

        while (response.nextLink != null) {
            deploymentsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiDeployments, response.nextLink, correlationId);
            response = new ApiDeploymentsResponse(deploymentsResponse);
            for (Deployment item : response.deployments) {
                if (ProcessDeployment(contentProvider, oldEnvironments, result, item, correlationId))
                    return result;
            }
        }

        return result;
    }

    private boolean ProcessDeployment(HttpContentProvider contentProvider, Environments oldEnvironments, Environments result, Deployment deployment, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, jetbrains.buildServer.serverSide.ProjectNotFoundException, ParseException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InvalidCacheConfigurationException {
        Environment lastKnownEnvironmentState = oldEnvironments.getEnvironment(deployment.environmentId);
        LOG.debug(String.format("%s: Found deployment to environment '%s' created at '%s'", correlationId, deployment.environmentId, deployment.createdDate));

        if (lastKnownEnvironmentState.isLatestDeploymentOlderThan(deployment.createdDate)) {
            LOG.debug(String.format("%s: Deployment to environment '%s' created at '%s' was newer than the last known deployment to this environment ('%s')", correlationId, deployment.environmentId, deployment.createdDate, lastKnownEnvironmentState.latestDeployment));

            String taskResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiTask, deployment.taskLink, correlationId);
            ApiTaskResponse task = new ApiTaskResponse(taskResponse);
            LOG.debug(String.format("%s: Deployment to environment '%s' created at '%s': isCompleted = '%s', finishedSuccessfully = '%s'", correlationId, deployment.environmentId, deployment.createdDate, task.isCompleted, task.finishedSuccessfully));

            if (task.isCompleted) {
                //todo: need some tests around this entire block, including the if
                String releaseResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRelease, deployment.releaseLink, correlationId);
                ApiReleaseResponse release = new ApiReleaseResponse(releaseResponse);

                Environment environment = Environment.CreateFrom(deployment, task, release);
                LOG.debug(String.format("%s: Updating results based on '%s'", correlationId, environment));
                result.addOrUpdate(environment);

                if (result.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment()) {
                    LOG.debug(String.format("%s: All deployments have finished successfully - no need to keep iterating", correlationId));
                    return true;
                }
            }
        } else {
            LOG.debug(String.format("%s: Deployment to environment '%s' created at '%s' was older than the last known deployment to this environment ('%s'", correlationId, deployment.environmentId, deployment.createdDate, lastKnownEnvironmentState.latestDeployment));
        }
        return false;
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
