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

package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Deployment;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentCompleteBuildTrigger;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Deployments;
import jetbrains.buildServer.serverSide.ProjectNotFoundException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

//todo: needs tests
//todo: consider if this should really go and get tasks
public class ApiDeploymentsResponse {
  private static final Logger LOG = Logger.getInstance(DeploymentCompleteBuildTrigger.class.getName());
  public final Deployments deployments;

  public ApiDeploymentsResponse(HttpContentProvider contentProvider, String deploymentsApiLink, String projectId, Deployments oldDeployments, Deployments newDeployments) throws URISyntaxException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, IOException, ParseException, java.text.ParseException, ProjectNotFoundException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, UnexpectedResponseCodeException {
    final String deploymentsResponse = contentProvider.getContent(deploymentsApiLink + "?Projects=" + projectId);

    this.deployments = ParseDeploymentResponse(contentProvider, deploymentsResponse, oldDeployments, newDeployments);
  }

  private Deployments ParseDeploymentResponse(HttpContentProvider contentProvider, String deploymentsResponse, Deployments oldDeployments, Deployments result) throws ParseException, java.text.ParseException, IOException, URISyntaxException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ProjectNotFoundException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException {
    LOG.debug("DeploymentCompleteBuildTrigger: parsing deployment response");
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(deploymentsResponse);

    List items = (List)response.get("Items");

    for (Object item : items) {
      if (ProcessDeployment(contentProvider, oldDeployments, result, (Map)item))
        return result;
    }

    Object nextPage = ((Map)response.get("Links")).get("Page.Next");
    if (null != nextPage) {
      final String nextPageResponse = contentProvider.getContent(nextPage.toString());
      final Deployments moreResults = ParseDeploymentResponse(contentProvider, nextPageResponse, oldDeployments, result);

      result.addOrUpdate(moreResults);
    }

    return result;
  }

  private boolean ProcessDeployment(HttpContentProvider contentProvider, Deployments oldDeployments, Deployments result, Map deployment) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, ParseException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException {
    String environmentId = deployment.get("EnvironmentId").toString();
    OctopusDate createdDate = new OctopusDate(deployment.get("Created").toString());
    Deployment lastKnownDeploymentForThisEnvironment = oldDeployments.getDeploymentForEnvironment(environmentId);
    LOG.debug("Found deployment to environment '" + environmentId + "' created at '" + createdDate + "'");

    if (lastKnownDeploymentForThisEnvironment.isLatestDeploymentOlderThan(createdDate)) {
      LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "' was newer than the last known deployment to this environment");

      GetTask(contentProvider, result, deployment, environmentId, createdDate);

      if (result.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment()) {
        LOG.debug("All deployments have finished successfully - no need to keep iterating");
        return true;
      }
    }
    else {
      LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "' was older than the last known deployment to this environment");
    }
    return false;
  }

  private void GetTask(HttpContentProvider contentProvider, Deployments result, Map deployment, String environmentId, OctopusDate createdDate) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, ParseException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException {
    String taskLink = ((Map) (deployment.get("Links"))).get("Task").toString();
    String taskResponse = contentProvider.getContent(taskLink);

    ApiTaskResponse task = new ApiTaskResponse(taskResponse);
    LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "': isCompleted = '" + task.isCompleted + "', finishedSuccessfully = '" + task.finishedSuccessfully + "'");

    result.addOrUpdate(environmentId, createdDate, task.isCompleted, task.finishedSuccessfully);
  }
}
