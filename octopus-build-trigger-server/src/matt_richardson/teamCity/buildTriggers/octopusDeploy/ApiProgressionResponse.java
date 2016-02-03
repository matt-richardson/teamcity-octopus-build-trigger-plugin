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

public class ApiProgressionResponse {
  //todo: log to own file, rather than server log
  private static final Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;
  Deployments deployments;
  Boolean haveCompleteInformation;

  public ApiProgressionResponse(String progressionResponse, Deployments oldDeployments) throws java.text.ParseException, ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, IOException {
    LOG.debug("OctopusBuildTrigger: parsing progression response");
    deployments = new Deployments(oldDeployments);
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(progressionResponse);
    SimpleDateFormat dateFormat = new SimpleDateFormat(OctopusDeploymentsProvider.OCTOPUS_DATE_FORMAT);//2015-12-08T08:09:39.624+00:00

    List environments = (List)response.get("Environments");
    for (Object environment : environments) {
      Map environmentMap = (Map)environment;
      deployments.addEnvironment(environmentMap.get("Id").toString());
    }

    List releasesAndDeployments = (List)response.get("Releases");

    if (releasesAndDeployments.size() == 0) {
      LOG.debug("No releases found");
      this.haveCompleteInformation = true;
      return;
    }

    Boolean foundDeployment = false;

    for (Object releaseAndDeploymentPair : releasesAndDeployments) {
      Map releaseAndDeploymentPairMap = (Map) releaseAndDeploymentPair;
      Map deps = (Map)releaseAndDeploymentPairMap.get("Deployments");
      for (Object key : deps.keySet()) {
        foundDeployment = true;
        Map deployment = (Map) deps.get(key);
        Date createdDate = dateFormat.parse(deployment.get("Created").toString());
        Boolean isCompleted = Boolean.parseBoolean(deployment.get("IsCompleted").toString());
        Boolean isSuccessful = deployment.get("State").toString().equals("Success");

        deployments.addOrUpdate((String)key, createdDate, isCompleted, isSuccessful);
      }
    }
    if (!foundDeployment) {
      LOG.debug("No deployments found");
      this.haveCompleteInformation = true;
      return;
    }

    if (deployments.haveAllDeploymentsFinishedSuccessfully()) {
      LOG.debug("All deployments have finished successfully - no need to parse deployment response");
      this.haveCompleteInformation = true;
      return;
    }

    this.haveCompleteInformation = false;
  }
}