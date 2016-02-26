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
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Test
public class DeploymentsProviderTest {
  final String octopusUrl = "http://baseUrl";
  final String octopusApiKey = "API-key";
  final String realOctopusUrl = "http://windows10vm.local/";
  final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

  static String ProjectWithNoDeployments = "Projects-23";
  static String ProjectWithLatestDeploymentSuccessful = "Projects-24";
  static String ProjectWithNoSuccessfulDeployments = "Projects-25";
  static String ProjectWithMultipleEnvironments = "Projects-28";
  static String ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful = "Projects-27";
  static String ProjectWithNoRecentSuccessfulDeployments = "Projects-26";
  static String ProjectWithNoReleases = "Projects-101";
  static String ProjectThatDoesNotExist = "Projects-00";

  @Test(enabled = false)
  public void testGetDeploymentsFromRealServer() throws Exception {
    HttpContentProvider contentProvider = new HttpContentProviderImpl(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
    Assert.assertNotNull(newDeployments);
  }

  public void testGetDeploymentsFromEmptyStart() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.environmentId, "Environments-1");

    //2015-12-08T08:09:39.624+00:00
    Assert.assertEquals(deployment.latestDeployment.toString(),
                        new OctopusDate(2016, 1, 21, 13, 31, 56, 22).toString(),
                        "Latest deployment is not as expected");
    //2015-11-12T09:22:00.865+00:00
    Assert.assertEquals(deployment.latestSuccessfulDeployment.toString(),
                        new OctopusDate(2016, 1, 21, 13, 31, 56, 22).toString(),
                        "Latest successful deployment is not as expected");
  }

  public void testGetDeploymentsFromEmptyStartWithNoReleases() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithNoReleases, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;1970-01-01T00:00:00.000+00:00;1970-01-01T00:00:00.000+00:00");
  }

  public void testGetDeploymentsFromEmptyStartWithNoDeployments() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithNoDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;1970-01-01T00:00:00.000+00:00;1970-01-01T00:00:00.000+00:00");
  }

  @Test(expectedExceptions = ProjectNotFoundException.class)
  public void testGetDeploymentsWithInvalidProject() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);

    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();

    deploymentsProvider.getDeployments(ProjectThatDoesNotExist, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidHost() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider("http://octopus.example.com", octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidPath() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
  public void testGetDeploymentsWithInvalidOctopusApiKey() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, "invalid-api-key");
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  public void testGetDeploymentsWhenUpToDate() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments("Environments-1;2016-01-21T13:31:56.022+00:00;2016-01-21T13:31:56.022+00:00");
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T13:31:56.022+00:00;2016-01-21T13:31:56.022+00:00");
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsHaveOccurred() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithNoSuccessfulDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T13:32:59.991+00:00;1970-01-01T00:00:00.000+00:00");
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsOnFirstPageOfResults() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithNoRecentSuccessfulDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:18:01.887+00:00;2016-01-21T13:35:27.179+00:00");
  }

  public void testGetDeploymentsWhenMultipleEnvironments() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);

    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithMultipleEnvironments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-21T14:25:40.247+00:00");
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00");
  }

  public void testGetDeploymentsWhenNoReleases() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithNoReleases, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;1970-01-01T00:00:00.000+00:00;1970-01-01T00:00:00.000+00:00");
  }

  public void testWhenThereAreTwoNewDeploymentsSinceLastCheckItReturnsOnlyOne() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
    Deployments oldDeployments = new Deployments(oldData);
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithMultipleEnvironments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-21T14:25:40.247+00:00");
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00");

    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-21T14:25:40.247+00:00");
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00");
  }

  public void testGetDeploymentsWhenMultipleEnvironmentsWithMostRecentDeploymentSuccessful() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProvider = new DeploymentsProviderImpl(contentProvider);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments(ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:24:30.935+00:00;2016-01-21T14:24:30.935+00:00");
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:24:10.872+00:00;2016-01-21T14:24:10.872+00:00");

  }

  private class FakeContentProvider implements HttpContentProvider {
    private final String octopusUrl;
    private String octopusApiKey;

    public FakeContentProvider(String octopusUrl, String octopusApiKey) {

      this.octopusUrl = octopusUrl;
      this.octopusApiKey = octopusApiKey;
    }

    public void close() {
      //no-op
    }

    public String getContent(String uriPath) throws IOException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, URISyntaxException, ProjectNotFoundException {
      String s = octopusUrl + uriPath;
      if (this.octopusUrl.contains("not-an-octopus-instance") || this.octopusUrl.contains("example.com")) {
        throw new InvalidOctopusUrlException(new URI(s)); //this is a bit odd, but we are just checking to make sure the right exception gets back to the right spot
      }
      if (!this.octopusApiKey.startsWith("API-")) {
        throw new InvalidOctopusApiKeyException(401, "Invalid octopus api key");
      }
      if (uriPath.endsWith("Projects-00")) {
        throw new ProjectNotFoundException("Projects-00");
      }

      try {
        final String resourceName = "/responses/3.3.0/" + s.replace(octopusUrl + "/", "").replace("?", "/") + ".json";
        InputStream resource = getClass().getResourceAsStream(resourceName);
        return IOUtils.toString(resource);
      } catch (IOException e) {
        throw new InvalidOctopusUrlException(new URI(s));
      }
    }

    public String getUrl() {
      return null;
    }
  }
}
