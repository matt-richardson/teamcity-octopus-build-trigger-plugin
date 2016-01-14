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

import org.apache.http.impl.client.CloseableHttpClient;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


final class OctopusDeploymentsProvider {

  static final String OCTOPUS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  private final HttpContentProvider contentProvider;

  public OctopusDeploymentsProvider() {
    this(new HttpContentProviderImpl());
  }

  public OctopusDeploymentsProvider(HttpContentProvider contentProvider)
  {
    this.contentProvider = contentProvider;
  }

  public Deployments getDeployments(String octopusUrl, String octopusApiKey, String octopusProject, Deployments oldDeployments) throws OctopusDeploymentsProviderException {
    //get {octopusurl}/api
    //parse out project url
    //parse out progression url
    //call project url
    //parse id for project
    //call progression url for project id
    //return NewDeploymentStatus(responseBody);

    CloseableHttpClient httpClient = null;

    try {
      final URI uri = new URL(octopusUrl).toURI();
      final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix

      contentProvider.init(octopusApiKey, connectionTimeout);

      final String apiResponse = contentProvider.getContent(new URL(octopusUrl + "/api").toURI());
      final String projectApiLink = parseLink(apiResponse, "Projects");
      final String deploymentsApiLink = parseLink(apiResponse, "Deployments");
      final String projectResponse = contentProvider.getContent(new URL(octopusUrl + projectApiLink).toURI());
      final String projectId = getProjectId(projectResponse, octopusProject);
      final String deploymentsResponse = contentProvider.getContent(new URL(octopusUrl + deploymentsApiLink + "?Projects=" + projectId).toURI());

      return ParseDeploymentResponse(contentProvider, octopusUrl, deploymentsResponse, oldDeployments);

    } catch (Throwable e) {
      throw new OctopusDeploymentsProviderException("URL " + octopusUrl + ": " + e.getMessage(), e);

    } finally {
      contentProvider.close(httpClient);
    }

  }

  private Deployments ParseDeploymentResponse(HttpContentProvider contentProvider, String octopusUrl, String deploymentsResponse, Deployments oldDeployments) throws ParseException, java.text.ParseException, IOException, URISyntaxException, UnexpectedResponseCodeException {
    Deployments result = new Deployments(oldDeployments.toString());
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(deploymentsResponse);

    List items = (List)response.get("Items");
    SimpleDateFormat dateFormat = new SimpleDateFormat(OCTOPUS_DATE_FORMAT);//2015-12-08T08:09:39.624+00:00

    for (Object item : items) {
      Map deployment = (Map)item;

      String environmentId = deployment.get("EnvironmentId").toString();
      Date createdDate = dateFormat.parse(deployment.get("Created").toString());
      Deployment lastKnownDeploymentForThisEnvironment = oldDeployments.getDeploymentForEnvironment(environmentId);
      if (lastKnownDeploymentForThisEnvironment.isLatestDeploymentOlderThen(createdDate)) {
        String taskLink = ((Map) (deployment.get("Links"))).get("Task").toString();
        String taskResponse = contentProvider.getContent(new URL(octopusUrl + taskLink).toURI());
        Map task = (Map)parser.parse(taskResponse);

        Boolean isCompleted = Boolean.parseBoolean(task.get("IsCompleted").toString());
        Boolean finishedSuccessfully = Boolean.parseBoolean(task.get("FinishedSuccessfully").toString());
        result.AddOrUpdate(environmentId, createdDate, isCompleted, finishedSuccessfully);
        if (result.haveAllDeploymentsFinishedSuccessfully())
          return result;
      }
    }

    Object nextPage = ((Map)response.get("Links")).get("Page.Next");
    if (null != nextPage) {
      final String nextPageResponse = contentProvider.getContent(new URL(octopusUrl + nextPage).toURI());
      final Deployments moreResults = ParseDeploymentResponse(contentProvider, octopusUrl, nextPageResponse, oldDeployments);

      result.AddOrUpdate(moreResults);
    }

    return result;
  }

  private String getProjectId(String projectResponse, String projectName) throws Exception {
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(projectResponse);
    List items = (List)response.get("Items");

    for (Object item: items) {
      Map map = (Map)item;
      if (map.get("Name").toString().equals(projectName)) {
        return map.get("Id").toString();
      }
    };
    throw new Exception("Unable to find project '" + projectName + "'");
  }

  private String parseLink(String apiResponse, String linkName) throws ParseException {
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(apiResponse);
    String link = (String)((Map)response.get("Links")).get(linkName);
    return link.replaceAll("\\{.*\\}", ""); //remove all optional params
  }

  public String checkOctopusConnectivity(String octopusUrl, String octopusApiKey) {
    CloseableHttpClient httpClient = null;

    try {
      final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix

      contentProvider.init(octopusApiKey, connectionTimeout);

      final String apiResponse = contentProvider.getContent(new URL(octopusUrl + "/api").toURI());

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
