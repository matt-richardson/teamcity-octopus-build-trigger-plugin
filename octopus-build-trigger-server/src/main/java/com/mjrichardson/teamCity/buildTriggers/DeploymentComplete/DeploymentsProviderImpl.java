/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

//todo: needs improved test coverage
public class DeploymentsProviderImpl implements DeploymentsProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentsProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;

    public DeploymentsProviderImpl(HttpContentProviderFactory httpContentProviderFactory) {
        this.httpContentProviderFactory = httpContentProviderFactory;
    }

    public Deployments getDeployments(String projectId, Deployments oldDeployments) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();
            LOG.debug("Getting deployments from " + contentProvider.getUrl() + " for project id '" + projectId + "'");

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider);
            final Project project = getProject(projectId, contentProvider, apiRootResponse);
            return getDeployments(projectId, oldDeployments, contentProvider, apiRootResponse, project);

        } catch (ProjectNotFoundException | InvalidOctopusApiKeyException | InvalidOctopusUrlException e) {
            throw e;
        } catch (Throwable e) {
            throw new DeploymentsProviderException(String.format("Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from %s: %s", url, e), e);
        }
    }

    private Deployments getDeployments(String projectId, Deployments oldDeployments, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, Project project) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, java.text.ParseException, ParseException {
        final String progressionResponse = contentProvider.getContent(project.progressionApiLink);
        final ApiProgressionResponse apiProgressionResponse = new ApiProgressionResponse(progressionResponse);

        if (apiProgressionResponse.haveCompleteInformation)
            return apiProgressionResponse.deployments;

        return getDeploymentsFromApi(projectId, oldDeployments, contentProvider, apiRootResponse, apiProgressionResponse);
    }

    private Project getProject(String projectId, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException {
        String projectsResponse = contentProvider.getContent(apiRootResponse.projectsApiLink);
        ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
        Projects projects = apiProjectsResponse.projects;
        while (shouldGetNextProjectsPage(apiProjectsResponse, projects, projectId)) {
            projectsResponse = contentProvider.getContent(apiProjectsResponse.nextLink);
            apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            Projects newProjects = apiProjectsResponse.projects;
            projects.add(newProjects);
        }
        return projects.getProject(projectId);
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException {
        final String apiResponse = contentProvider.getContent("/api");
        return new ApiRootResponse(apiResponse);
    }

    @NotNull
    private Deployments getDeploymentsFromApi(String projectId, Deployments oldDeployments, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, ApiProgressionResponse apiProgressionResponse) throws URISyntaxException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, IOException, ParseException, java.text.ParseException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String deploymentsResponse = contentProvider.getContent(apiRootResponse.deploymentsApiLink + "?Projects=" + projectId);
        ApiDeploymentsResponse response = new ApiDeploymentsResponse(deploymentsResponse);
        Deployments result = new Deployments();
        for (OctopusDeployment item : response.octopusDeployments) {
            if (ProcessDeployment(contentProvider, oldDeployments, result, item))
                return result;
        }

        while (response.nextLink != null) {
            deploymentsResponse = contentProvider.getContent(response.nextLink);
            response = new ApiDeploymentsResponse(deploymentsResponse);
            for (OctopusDeployment item : response.octopusDeployments) {
                if (ProcessDeployment(contentProvider, oldDeployments, result, item))
                    return result;
            }
        }

        return result;
    }

    private boolean ProcessDeployment(HttpContentProvider contentProvider, Deployments oldDeployments, Deployments result, OctopusDeployment octopusDeployment) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, jetbrains.buildServer.serverSide.ProjectNotFoundException, ParseException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Deployment lastKnownDeploymentForThisEnvironment = oldDeployments.getDeploymentForEnvironment(octopusDeployment.environmentId);
        LOG.debug("Found deployment to environment '" + octopusDeployment.environmentId + "' created at '" + octopusDeployment.createdDate + "'");

        if (lastKnownDeploymentForThisEnvironment.isLatestDeploymentOlderThan(octopusDeployment.createdDate)) {
            LOG.debug("Deployment to environment '" + octopusDeployment.environmentId + "' created at '" + octopusDeployment.createdDate + "' was newer than the last known deployment to this environment");

            String taskResponse = contentProvider.getContent(octopusDeployment.taskLink);
            ApiTaskResponse task = new ApiTaskResponse(taskResponse);
            LOG.debug("Deployment to environment '" + octopusDeployment.environmentId + "' created at '" + octopusDeployment.createdDate + "': isCompleted = '" + task.isCompleted + "', finishedSuccessfully = '" + task.finishedSuccessfully + "'");

            result.addOrUpdate(octopusDeployment.environmentId, octopusDeployment.createdDate, task.isCompleted, task.finishedSuccessfully);

            if (result.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment()) {
                LOG.debug("All deployments have finished successfully - no need to keep iterating");
                return true;
            }
        } else {
            LOG.debug("Deployment to environment '" + octopusDeployment.environmentId + "' created at '" + octopusDeployment.createdDate + "' was older than the last known deployment to this environment");
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
