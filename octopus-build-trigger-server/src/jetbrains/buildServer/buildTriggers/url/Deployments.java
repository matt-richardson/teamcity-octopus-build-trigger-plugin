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

package jetbrains.buildServer.buildTriggers.url;

import jetbrains.buildServer.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Deployments {
  private ArrayList<Deployment> statusMap;

  public Deployments(String oldStoredData) throws ParseException {
    this.statusMap = new ArrayList<Deployment>();

    if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(OctopusDeploymentsProvider.OCTOPUS_DATE_FORMAT);
      for (String pair : oldStoredData.split("\\|")) {
        if (pair.length() > 0) {
          final String[] split = pair.split(";");
          final String environmentId = split[0];
          final Date latestDeployment = dateFormat.parse(split[1]);
          final Date latestSuccessfulDeployment = dateFormat.parse(split[2]);
          statusMap.add(new Deployment(environmentId, latestDeployment, latestSuccessfulDeployment));
        }
      }
    }
  }

  public Deployments() throws ParseException {
    this("");
  }

  @Override
  public String toString() {
    String result = "";

    for (Deployment deployment: statusMap) {
      result = String.format("%s%s|", result, deployment.toString());
    }
    return result.replaceAll("\\|+$", "");
  }

  public boolean isEmpty() {
    return statusMap.size() == 0;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    final Deployments other = (Deployments) obj;

    if (this.statusMap.size() != other.statusMap.size()) {
      return false;
    }

    for (Deployment deployment: statusMap) {
      Boolean found = false;
      for (Deployment otherDeployment: other.statusMap) {
        if (otherDeployment.environmentId.equals(deployment.environmentId)) {
          found = true;
          if (!otherDeployment.latestDeployment.equals(deployment.latestDeployment)) {
            return false;
          }
          if (!otherDeployment.latestSuccessfulDeployment.equals(deployment.latestSuccessfulDeployment)) {
            return false;
          }
        }
      }
      if (!found) {
        return false;
      }
    }

    return true;
  }

  public Deployment getDeploymentForEnvironment(String environmentId) {
    for (Deployment deployment: statusMap) {
      if (deployment.environmentId.equals(environmentId))
      {
        return deployment;
      }
    }
    return new NullDeployment();
  }

  public int length() {
    return statusMap.size();
  }

  public void AddOrUpdate(String environmentId, Date latestDeployment, Boolean isCompleted, Boolean finishedSuccessfully) {
    if (!isCompleted)
      return;

    Deployment targetDeployment = getDeploymentForEnvironment(environmentId);
    if (targetDeployment.getClass().equals(NullDeployment.class)) {
      targetDeployment = new Deployment(environmentId, latestDeployment, finishedSuccessfully ? latestDeployment : new Date(0));
      statusMap.add(targetDeployment);
    }
    else {
      if (targetDeployment.isLatestDeploymentOlderThen(latestDeployment)) {
        targetDeployment.latestDeployment = latestDeployment;
      }
      if (finishedSuccessfully && targetDeployment.isLatestSuccessfulDeploymentOlderThen(latestDeployment)) {
        targetDeployment.latestSuccessfulDeployment = latestDeployment;
      }
    }
  }

  public void AddOrUpdate(Deployments moreResults) {
    for (Deployment deployment : moreResults.statusMap) {
      AddOrUpdate(deployment.environmentId, deployment.latestDeployment, deployment.latestSuccessfulDeployment);
    }
  }

  private void AddOrUpdate(String environmentId, Date latestDeployment, Date latestSuccessfulDeployment) {
    Deployment targetDeployment = getDeploymentForEnvironment(environmentId);
    if (targetDeployment.getClass().equals(NullDeployment.class)) {
      targetDeployment = new Deployment(environmentId, latestDeployment, latestSuccessfulDeployment);
      statusMap.add(targetDeployment);
    }
    else {
      if (targetDeployment.isLatestDeploymentOlderThen(latestDeployment)) {
        targetDeployment.latestDeployment = latestDeployment;
      }
      if (targetDeployment.isLatestSuccessfulDeploymentOlderThen(latestSuccessfulDeployment)) {
        targetDeployment.latestSuccessfulDeployment = latestSuccessfulDeployment;
      }
    }
  }
}
