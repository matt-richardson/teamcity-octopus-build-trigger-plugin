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
}
