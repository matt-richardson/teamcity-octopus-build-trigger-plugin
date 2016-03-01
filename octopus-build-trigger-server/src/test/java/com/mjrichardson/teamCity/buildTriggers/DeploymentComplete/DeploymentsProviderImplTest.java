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

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

//todo: rename tests to use underscores
@Test
public class DeploymentsProviderImplTest {
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
  public void testGetDeploymentsFromRealServer() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments("Project with latest deployment successful", oldDeployments);
    Assert.assertNotNull(newDeployments);
  }

  public void testGetDeploymentsFromEmptyStart() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
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

  public void testGetDeploymentsFromEmptyStartWithNoReleases() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithNoReleases, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1)));
  }

  public void testGetDeploymentsFromEmptyStartWithNoDeployments() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithNoDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1)));
  }

  @Test(expectedExceptions = ProjectNotFoundException.class)
  public void testGetDeploymentsWithInvalidProject() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();

    deploymentsProviderImpl.getDeployments(ProjectThatDoesNotExist, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidHost() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidPath() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
  public void testGetDeploymentsWithInvalidOctopusApiKey() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  @Test(expectedExceptions = DeploymentsProviderException.class)
  public void rethrows_throwable_exceptions_as_deployment_provider_exception() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
  }

  public void testGetDeploymentsWhenUpToDate() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments("Environments-1;2016-01-21T13:31:56.022+00:00;2016-01-21T13:31:56.022+00:00");
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 13, 31, 56, 22), new OctopusDate(2016, 1, 21, 13, 31, 56, 22)));
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsHaveOccurred() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithNoSuccessfulDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 13, 32, 59, 991), new OctopusDate(1970, 1, 1)));
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsOnFirstPageOfResults() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithNoRecentSuccessfulDeployments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 14, 18, 1, 887), new OctopusDate(2016, 1, 21, 13, 35, 27, 179)));
  }

  public void testGetDeploymentsWhenMultipleEnvironments() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247)));
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700)));
  }

  public void testGetDeploymentsWhenNoReleases() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithNoReleases, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1)));
  }

  public void testWhenThereAreTwoNewDeploymentsSinceLastCheckItReturnsOnlyOne() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
    Deployments oldDeployments = new Deployments(oldData);
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironments, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247)));
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700)));

    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247)));
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0, 0)));
  }

  public void testGetDeploymentsWhenMultipleEnvironmentsWithMostRecentDeploymentSuccessful() throws Exception {
    HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
    DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful, oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-1", new OctopusDate(2016, 1, 21, 14, 24, 30, 935), new OctopusDate(2016, 1, 21, 14, 24, 30, 935)));
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment, new Deployment("Environments-21", new OctopusDate(2016, 1, 21, 14, 24, 10, 872), new OctopusDate(2016, 1, 21, 14, 24, 10, 872)));

  }
}
