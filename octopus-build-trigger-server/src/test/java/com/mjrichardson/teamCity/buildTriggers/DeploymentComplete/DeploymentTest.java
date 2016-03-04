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

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;


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

  public void parse_returns_null_deployment_if_is_not_completed() {
    HashMap<String, String> map = new HashMap<>();
    map.put("EnvironmentId", "Environment-1");
    map.put("Created", "2016-02-26T17:58:13.537+00:00");
    map.put("IsCompleted", "false");
    map.put("State", "Unknown");
    Deployment sut = Deployment.Parse(map);
    Assert.assertEquals(sut.getClass(), NullDeployment.class);
  }

  public void parse_returns_deployment_with_valid_latest_successful_deployment_if_successful() {
    HashMap<String, String> map = new HashMap<>();
    map.put("EnvironmentId", "Environment-1");
    map.put("Created", "2016-02-26T17:58:13.537+00:00");
    map.put("IsCompleted", "true");
    map.put("State", "Success");
    Deployment sut = Deployment.Parse(map);
    Assert.assertEquals(sut.environmentId, "Environment-1");
    Assert.assertEquals(sut.latestDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
    Assert.assertEquals(sut.latestSuccessfulDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
  }

  public void parse_returns_deployment_with_null_latest_successful_deployment_if_not_successful() {
    HashMap<String, String> map = new HashMap<>();
    map.put("EnvironmentId", "Environment-1");
    map.put("Created", "2016-02-26T17:58:13.537+00:00");
    map.put("IsCompleted", "true");
    map.put("State", "Failed");
    Deployment sut = Deployment.Parse(map);
    Assert.assertEquals(sut.environmentId, "Environment-1");
    Assert.assertEquals(sut.latestDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
    Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
  }

  public void is_latest_successful_deployment_newer_than_returns_false_if_passed_date_is_newer() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
    Assert.assertFalse(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 27)));
  }

  public void is_latest_successful_deployment_newer_than_returns_false_if_passed_date_is_same() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
    Assert.assertFalse(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 26)));
  }

  public void is_latest_successful_deployment_newer_than_returns_true_if_passed_date_is_older() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
    Assert.assertTrue(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 25)));
  }

  public void two_param_ctor_sets_latest_successful_date_to_null() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26));
    Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
  }

  public void is_successful_returns_true_if_dates_are_the_same() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
    Assert.assertTrue(sut.isSuccessful());
  }

  public void is_successful_returns_false_if_dates_are_different() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 25));
    Assert.assertFalse(sut.isSuccessful());
  }

  public void has_had_at_least_one_successful_deployment_returns_true_if_date_of_last_successful_deployment_is_newer_than_1970() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 25));
    Assert.assertTrue(sut.hasHadAtLeastOneSuccessfulDeployment());
  }

  public void has_had_at_least_one_successful_deployment_returns_false_if_date_of_last_successful_deployment_is_1970() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
    Assert.assertFalse(sut.hasHadAtLeastOneSuccessfulDeployment());
  }

  public void equals_returns_false_when_other_object_is_not_a_deployment() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
    Assert.assertFalse(sut.equals(new Deployments()));
  }

  public void equals_returns_false_when_other_object_is_null() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
    Assert.assertFalse(sut.equals(null));
  }

  public void equals_returns_true_when_both_objects_are_same() {
    Deployment sut = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
    Deployment other = new Deployment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
    Assert.assertTrue(sut.equals(other));
  }
}
