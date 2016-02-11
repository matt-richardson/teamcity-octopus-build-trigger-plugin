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

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DeploymentsTest {
  public void can_convert_single_environment_from_string_and_back_again() throws Exception {
    final String expected = "Environments-1;2015-12-08T08:09:39.624Z;2015-11-12T09:22:00.865Z";
    Deployments deployments = new Deployments(expected);
    Assert.assertEquals(deployments.toString(), expected);
  }

  public void can_convert_multiple_environments_from_string_and_back_again() throws Exception {
    final String expected = "Environments-1;2015-12-08T08:09:39.624Z;2015-11-12T09:22:00.865Z|Environments-2;2015-12-07T14:12:14.624Z;2015-12-07T14:12:14.624Z";
    Deployments deployments = new Deployments(expected);
    Assert.assertEquals(deployments.toString(), expected);
  }

  public void can_convert_from_empty_string_and_back_again() throws Exception {
    Deployments deployments = new Deployments("");
    Assert.assertEquals(deployments.toString(), "");
  }

  public void is_empty_returns_true_when_no_deployments() throws Exception {
    Deployments deployments = new Deployments("");
    Assert.assertTrue(deployments.isEmpty());
  }

  public void is_empty_returns_false_when_has_deployments() throws Exception {
    final String expected = "Environments-1;2015-12-08T08:09:39.624Z;2015-11-12T09:22:00.865Z|Environments-2;2015-12-07T14:12:14.624Z;2015-12-07T14:12:14.624Z";
    Deployments deployments = new Deployments(expected);
    Assert.assertFalse(deployments.isEmpty());
  }

  public void trim_multiple_deployments_to_return_only_one_changed_environment() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-21T14:26:14.747Z;2016-01-21T14:25:40.247Z|Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z";
    Deployments newDeployments = new Deployments(newData);

    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    Deployment deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747Z;2016-01-21T14:25:40.247Z");
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z");
  }

  public void trim_multiple_deployments_to_return_only_one_changed_environment_can_prioritise_successful_deployments() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-21T14:26:14.747Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z";
    Deployments newDeployments = new Deployments(newData);

    final Boolean prioritiseSuccessfulDeployments = true;
    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, prioritiseSuccessfulDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    Deployment deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z");
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z");
  }

  public void trim_multiple_deployments_to_return_only_one_changed_environment_can_skip_successful_deployment_prioritisation() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-21T14:26:14.747Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z";
    Deployments newDeployments = new Deployments(newData);

    final Boolean prioritiseSuccessfulDeployments = false;
    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, prioritiseSuccessfulDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    Deployment deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747Z;2016-01-19T00:00:00.000Z");
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z");
  }

  public void trim_multiple_deployments_to_return_only_one_changed_environment_returns_input_when_none_changed() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments newDeployments = new Deployments(newData);

    final Boolean prioritiseSuccessfulDeployments = true;
    final Deployments trimmedDeployments = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, prioritiseSuccessfulDeployments);
    Assert.assertEquals(trimmedDeployments.length(), 2);
    Deployment deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z");
    deployment = trimmedDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z");
  }

  public void get_changed_deployment_returns_first_environment_thats_changed() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z";
    Deployments newDeployments = new Deployments(newData);

    Deployment deployment = newDeployments.getChangedDeployment(oldDeployments);
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z");
  }

  @Test(expectedExceptions = NoChangedDeploymentsException.class)
  public void get_changed_deployment_throws_exception_when_none_changed() throws Exception {
    final String oldData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments oldDeployments = new Deployments(oldData);
    final String newData = "Environments-1;2016-01-19T14:00:00.000Z;2016-01-19T00:00:00.000Z|Environments-21;2016-01-20T14:00:00.000Z;2016-01-20T14:00:00.000Z";
    Deployments newDeployments = new Deployments(newData);

    newDeployments.getChangedDeployment(oldDeployments);
  }
}
