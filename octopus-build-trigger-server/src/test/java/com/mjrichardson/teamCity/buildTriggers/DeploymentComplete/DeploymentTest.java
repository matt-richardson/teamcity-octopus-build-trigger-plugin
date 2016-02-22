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

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;


@Test
public class DeploymentTest {
  public void isLatestDeploymentOlderThanReturnsTrueWhenNewerDatePassed() {
    OctopusDate testDate = new OctopusDate(2015, 12, 10);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), true);
  }

  public void isLatestDeploymentOlderThanReturnsFalseWhenOlderDatePassed() {
    OctopusDate testDate = new OctopusDate(2015, 12, 8);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), false);
  }

  public void isLatestDeploymentOlderThanReturnsFalseWhenSameDatePassed() {
    OctopusDate testDate = new OctopusDate(2015, 12, 9);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), false);
  }

  public void isLatestDeploymentOlderThanComparesAgainstLatestDeploymentDate() {
    OctopusDate testDate = new OctopusDate(2015, 12, 10);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 12));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), true);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsTrueWhenNewerDatePassed() {
    //new OctopusDate(2014, Calendar.FEBRUARY, 11).getTime(
    OctopusDate testDate = new OctopusDate(2015, 12, 10);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), true);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenOlderDatePassed() {
    OctopusDate testDate = new OctopusDate(2015, 12, 8);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), false);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenSameDatePassed() {
    OctopusDate testDate = new OctopusDate(2015, 12, 9);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestDeploymentOlderThan(testDate), false);
  }

  public void isLatestSuccessfulDeploymentOlderThenComparesAgainstLatestDeploymentDate() {
    OctopusDate testDate = new OctopusDate(2015, 12, 10);
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 12), new OctopusDate(2015, 12, 9));
    Assert.assertEquals(deployment.isLatestSuccessfulDeploymentOlderThen(testDate), true);
  }

  public void toStringFormatsCorrectly() {
    Deployment deployment = new Deployment("env", new OctopusDate(2015, 12, 9, 14, 10, 11), new OctopusDate(2015, 12, 12, 9, 4, 3));
    Assert.assertEquals(deployment.toString(), "env;2015-12-09T14:10:11.000+00:00;2015-12-12T09:04:03.000+00:00");
  }
}
