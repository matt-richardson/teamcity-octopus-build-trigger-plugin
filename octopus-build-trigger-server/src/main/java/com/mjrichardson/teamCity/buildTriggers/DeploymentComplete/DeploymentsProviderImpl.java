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

            final String apiResponse = contentProvider.getContent("/api");
            final ApiRootResponse apiRootResponse = new ApiRootResponse(apiResponse);

            //todo: move to a builder pattern for loading up results
            String projectsResponse = contentProvider.getContent(apiRootResponse.projectsApiLink);
            final ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            while (shouldGetNextProjectsPage(apiProjectsResponse, projectId)) {
                projectsResponse = contentProvider.getContent(apiProjectsResponse.nextLink);
                apiProjectsResponse.add(new ApiProjectReleasesResponse(projectsResponse));
            }
            Project project = apiProjectsResponse.getProject(projectId);

            final String progressionResponse = contentProvider.getContent(project.progressionApiLink);
            final ApiProgressionResponse apiProgressionResponse = new ApiProgressionResponse(progressionResponse);

            if (apiProgressionResponse.haveCompleteInformation)
                return apiProgressionResponse.deployments;

            final ApiDeploymentsResponse apiDeploymentsResponse = new ApiDeploymentsResponse(
                    contentProvider, apiRootResponse.deploymentsApiLink, projectId,
                    oldDeployments, apiProgressionResponse.deployments);

            return apiDeploymentsResponse.deployments;
        } catch (InvalidOctopusApiKeyException e) {
            throw e;
        } catch (InvalidOctopusUrlException e) {
            throw e;
        } catch (ProjectNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new DeploymentsProviderException(String.format("Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from %s: %s", url, e), e);
        }
    }

    private boolean shouldGetNextProjectsPage(ApiProjectsResponse apiProjectsResponse, String projectId) {
        if (apiProjectsResponse.isEmpty())
            return false;
        if (apiProjectsResponse.projects.contains(projectId))
            return false;
        if (apiProjectsResponse.nextLink == null)
            return false;
        return true;
    }
}
