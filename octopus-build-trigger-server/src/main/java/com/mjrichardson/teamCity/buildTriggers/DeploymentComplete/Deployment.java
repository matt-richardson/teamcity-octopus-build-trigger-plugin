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

import java.util.Map;

public class Deployment {
  public final String environmentId;
  public OctopusDate latestDeployment;//todo:consider if we can make this class idempotent
  public OctopusDate latestSuccessfulDeployment;

  public Deployment(String environmentId, OctopusDate latestDeployment) {
    this(environmentId, latestDeployment, new NullOctopusDate());
  }

  public Deployment(String environmentId, OctopusDate latestDeployment, OctopusDate latestSuccessfulDeployment) {
    this.environmentId = environmentId;
    this.latestDeployment = latestDeployment;
    this.latestSuccessfulDeployment = latestSuccessfulDeployment;
  }

  public boolean isLatestDeploymentOlderThan(OctopusDate compareDate) {
    return this.latestDeployment.compareTo(compareDate) < 0;
  }

  public boolean isLatestSuccessfulDeploymentOlderThen(OctopusDate compareDate) {
    return this.latestSuccessfulDeployment.compareTo(compareDate) < 0;
  }

  public boolean isSuccessful() {
    return this.latestSuccessfulDeployment.compareTo(this.latestDeployment) == 0;
  }

  public boolean hasHadAtLeastOneSuccessfulDeployment() {
    return this.latestSuccessfulDeployment.compareTo(new NullOctopusDate()) > 0;
  }

  @Override
  public String toString() {
    return String.format("%s;%s;%s", environmentId, latestDeployment, latestSuccessfulDeployment);
  }

  public boolean isLatestSuccessfulDeploymentNewerThan(OctopusDate compareDate) {
    return this.latestSuccessfulDeployment.compareTo(compareDate) > 0;
  }

  public static Deployment Parse(Map map) {
    OctopusDate createdDate = new OctopusDate(map.get("Created").toString());
    Boolean isCompleted = Boolean.parseBoolean(map.get("IsCompleted").toString());
    Boolean isSuccessful = map.get("State").toString().equals("Success");
    String environmentId = map.get("EnvironmentId").toString();

    if (!isCompleted)
      return new NullDeployment();
    if (isSuccessful)
      return new Deployment(environmentId, createdDate, createdDate);
    return new Deployment(environmentId, createdDate);
  }
}
