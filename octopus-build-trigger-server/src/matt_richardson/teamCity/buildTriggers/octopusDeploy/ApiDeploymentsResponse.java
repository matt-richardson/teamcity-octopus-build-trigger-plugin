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

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import com.intellij.openapi.diagnostic.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiDeploymentsResponse {
  //todo: log to own file, rather than server log
  private static final Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;
  final Deployments deployments;

  public ApiDeploymentsResponse(HttpContentProvider contentProvider, String deploymentsApiLink, String projectId, Deployments oldDeployments, Deployments newDeployments) throws URISyntaxException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, IOException, ParseException, java.text.ParseException {
    final String deploymentsResponse = contentProvider.getContent(deploymentsApiLink + "?Projects=" + projectId);

    this.deployments = ParseDeploymentResponse(contentProvider, deploymentsResponse, oldDeployments, newDeployments);
  }

  private Deployments ParseDeploymentResponse(HttpContentProvider contentProvider, String deploymentsResponse, Deployments oldDeployments, Deployments result) throws ParseException, java.text.ParseException, IOException, URISyntaxException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    LOG.debug("OctopusBuildTrigger: parsing deployment response");
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(deploymentsResponse);

    List items = (List)response.get("Items");
    SimpleDateFormat dateFormat = new SimpleDateFormat(OctopusDeploymentsProvider.OCTOPUS_DATE_FORMAT);//2015-12-08T08:09:39.624+00:00

    for (Object item : items) {
      Map deployment = (Map)item;

      String environmentId = deployment.get("EnvironmentId").toString();
      Date createdDate = dateFormat.parse(deployment.get("Created").toString());
      Deployment lastKnownDeploymentForThisEnvironment = oldDeployments.getDeploymentForEnvironment(environmentId);
      LOG.debug("Found deployment to environment '" + environmentId + "' created at '" + createdDate + "'");
      if (lastKnownDeploymentForThisEnvironment.isLatestDeploymentOlderThan(createdDate)) {
        LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "' was newer than the last known deployment to this environment");
        String taskLink = ((Map) (deployment.get("Links"))).get("Task").toString();
        String taskResponse = contentProvider.getContent(taskLink);
        Map task = (Map)parser.parse(taskResponse);

        Boolean isCompleted = Boolean.parseBoolean(task.get("IsCompleted").toString());
        Boolean finishedSuccessfully = Boolean.parseBoolean(task.get("FinishedSuccessfully").toString());
        LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "': isCompleted = '" + isCompleted + "', finishedSuccessfully = '" + finishedSuccessfully + "'");
        result.addOrUpdate(environmentId, createdDate, isCompleted, finishedSuccessfully);
        if (result.haveAllDeploymentsFinishedSuccessfully()) {
          LOG.debug("All deployments have finished successfully - no need to keep iterating");
          return result;
        }
      }
      else {
        LOG.debug("Deployment to environment '" + environmentId + "' created at '" + createdDate + "' was older than the last known deployment to this environment");
      }
    }

    Object nextPage = ((Map)response.get("Links")).get("Page.Next");
    if (null != nextPage) {
      final String nextPageResponse = contentProvider.getContent(nextPage.toString());
      final Deployments moreResults = ParseDeploymentResponse(contentProvider, nextPageResponse, oldDeployments, result);

      result.addOrUpdate(moreResults);
    }

    return result;
  }

}
