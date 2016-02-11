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
import org.apache.http.impl.client.CloseableHttpClient;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


final class OctopusDeploymentsProvider {

  static final String OCTOPUS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  private final HttpContentProvider contentProvider;
  private final Logger LOG;

  public OctopusDeploymentsProvider(String octopusUrl, String apiKey, Integer connectionTimeout, Logger log) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    this(new HttpContentProviderImpl(log, octopusUrl, apiKey, connectionTimeout), log);
  }

  public OctopusDeploymentsProvider(HttpContentProvider contentProvider, Logger log)
  {
    this.contentProvider = contentProvider;
    this.LOG = log;
  }

  public Deployments getDeployments(String octopusProject, Deployments oldDeployments) throws OctopusDeploymentsProviderException {
    //get {octopusurl}/api
    //parse out project url
    //parse out progression url
    //call project url
    //parse id for project
    //call progression url for project id
    //return NewDeploymentStatus(responseBody);


    try {
      LOG.debug("OctopusBuildTrigger: Getting deployments from " + contentProvider.getUrl() + " for project " + octopusProject);

      final String apiResponse = contentProvider.getContent("/api");
      final ApiRootResponse apiRootResponse = new ApiRootResponse(apiResponse);
      final String projectResponse = contentProvider.getContent(apiRootResponse.projectApiLink);
      final ApiProjectResponse apiProjectResponse = new ApiProjectResponse(projectResponse, octopusProject);

      final String progressionResponse = contentProvider.getContent(apiRootResponse.progressionApiLink + apiProjectResponse.projectId);
      final ApiProgressionResponse apiProgressionResponse = new ApiProgressionResponse(progressionResponse);

      if (apiProgressionResponse.haveCompleteInformation)
        return apiProgressionResponse.deployments;

      final ApiDeploymentsResponse apiDeploymentsResponse = new ApiDeploymentsResponse(
        contentProvider, apiRootResponse.deploymentsApiLink, apiProjectResponse.projectId,
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
      throw new OctopusDeploymentsProviderException("URL " + contentProvider.getUrl() + ": " + e, e);
    }
  }

  public String checkOctopusConnectivity() {
    CloseableHttpClient httpClient = null;

    try {
      LOG.info("OctopusBuildTrigger: checking connectivity to octopus at " + contentProvider.getUrl());
      contentProvider.getContent("/api");

      return null;

    } catch (UnexpectedResponseCodeException e) {
      return e.getMessage();
    } catch (Throwable e) {
      return e.getMessage();
    } finally {
      contentProvider.close(httpClient);
    }
  }

}
