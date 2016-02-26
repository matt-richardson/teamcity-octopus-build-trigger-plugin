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
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentCompleteBuildTriggerService;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Deployments;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ApiProgressionResponse {
  private static final Logger LOG = Logger.getInstance(DeploymentCompleteBuildTriggerService.class.getName());
  public Deployments deployments;
  public Boolean haveCompleteInformation;

  public ApiProgressionResponse(String progressionResponse) throws java.text.ParseException, ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, IOException {
    LOG.debug("DeploymentCompleteBuildTriggerService: parsing progression response");
    this.haveCompleteInformation = Parse(progressionResponse);
  }

  private boolean Parse(String progressionResponse) throws java.text.ParseException, ParseException {
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(progressionResponse);

    deployments = new Deployments();
    List environments = (List)response.get("Environments");
    for (Object environment : environments) {
      Map environmentMap = (Map)environment;
      deployments.addEnvironment(environmentMap.get("Id").toString());
    }

    List releasesAndDeployments = (List)response.get("Releases");

    if (releasesAndDeployments.size() == 0) {
      LOG.debug("No releases found in progression api response");
      return true;
    }

    Boolean foundDeployment = AddDeployments(releasesAndDeployments);
    if (!foundDeployment) {
      LOG.debug("No deployments found in progression api response");
      return true;
    }

    if (deployments.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment()) {
      LOG.debug("All deployments have finished successfully - no need to parse deployment response");
      return true;
    }

    return false;
  }

  private Boolean AddDeployments(List releasesAndDeployments) throws java.text.ParseException {
    Boolean foundDeployment = false;

    for (Object releaseAndDeploymentPair : releasesAndDeployments) {
      Map releaseAndDeploymentPairMap = (Map) releaseAndDeploymentPair;
      Map deps = (Map)releaseAndDeploymentPairMap.get("Deployments");
      for (Object key : deps.keySet()) {
        foundDeployment = true;
        Deployment deployment = Deployment.Parse((Map) deps.get(key));
        deployments.addOrUpdate(deployment);
      }
    }
    return foundDeployment;
  }
}
