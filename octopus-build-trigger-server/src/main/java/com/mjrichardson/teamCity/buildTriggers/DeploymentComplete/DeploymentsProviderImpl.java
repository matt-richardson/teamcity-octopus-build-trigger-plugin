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

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

//todo needs tests
public class DeploymentsProviderImpl implements DeploymentsProvider {

  private final HttpContentProvider contentProvider;
  @NotNull
  private static final Logger LOG = Logger.getInstance(DeploymentsProviderImpl.class.getName());

  public DeploymentsProviderImpl(String octopusUrl, String apiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    this(new HttpContentProviderImpl(octopusUrl, apiKey, connectionTimeout));
  }

  public DeploymentsProviderImpl(HttpContentProvider contentProvider)
  {
    this.contentProvider = contentProvider;
  }

  public Deployments getDeployments(String projectId, Deployments oldDeployments) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    //get {octopusurl}/api
    //parse out project url
    //parse out progression url
    //call project url
    //parse id for project
    //call progression url for project id
    //return NewDeploymentStatus(responseBody);

    try {
      LOG.debug("DeploymentCompleteBuildTriggerService: Getting deployments from " + contentProvider.getUrl() + " for project id '" + projectId + "'");

      final String apiResponse = contentProvider.getContent("/api");
      final ApiRootResponse apiRootResponse = new ApiRootResponse(apiResponse);

      final String progressionResponse = contentProvider.getContent(apiRootResponse.progressionApiLink + "/" + projectId);
      final ApiProgressionResponse apiProgressionResponse = new ApiProgressionResponse(progressionResponse);

      if (apiProgressionResponse.haveCompleteInformation)
        return apiProgressionResponse.deployments;

      final ApiDeploymentsResponse apiDeploymentsResponse = new ApiDeploymentsResponse(
        contentProvider, apiRootResponse.deploymentsApiLink, projectId,
        oldDeployments, apiProgressionResponse.deployments);

      return apiDeploymentsResponse.deployments;
    }
    catch (InvalidOctopusApiKeyException e) {
      throw e;
    }
    catch (InvalidOctopusUrlException e) {
      throw e;
    }
    catch (ProjectNotFoundException e) {
      throw e;
    }
    catch (Throwable e) {
      //todo: improve error message here
      throw new DeploymentsProviderException("URL " + contentProvider.getUrl() + ": " + e, e);
    }
  }
}
