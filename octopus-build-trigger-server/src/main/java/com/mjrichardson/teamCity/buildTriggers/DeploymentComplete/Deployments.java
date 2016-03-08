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
import jetbrains.buildServer.util.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;

public class Deployments {
    private ArrayList<Deployment> statusMap;

    public Deployments() {
        this.statusMap = new ArrayList<>();
    }

    public Deployments(String oldStoredData) throws ParseException {
        this();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {

            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    final String[] split = pair.split(";");
                    final String environmentId = split[0];
                    final OctopusDate latestDeployment = OctopusDate.Parse(split[1]);
                    final OctopusDate latestSuccessfulDeployment = OctopusDate.Parse(split[2]);
                    statusMap.add(new Deployment(environmentId, latestDeployment, latestSuccessfulDeployment));
                }
            }
        }
    }

    public Deployments(Deployments oldDeployments) {
        this();
        addOrUpdate(oldDeployments);
    }

    public Deployments(Deployment deployment) {
        this();
        addOrUpdate(deployment);
    }

    @Override
    public String toString() {
        String result = "";

        for (Deployment deployment : statusMap) {
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

        for (Deployment deployment : statusMap) {
            Boolean found = false;
            for (Deployment otherDeployment : other.statusMap) {
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
        for (Deployment deployment : statusMap) {
            if (deployment.environmentId.equals(environmentId)) {
                return deployment;
            }
        }
        return new NullDeployment();
    }

    public void addOrUpdate(String environmentId, OctopusDate latestDeployment, Boolean isCompleted, Boolean finishedSuccessfully) {
        if (!isCompleted)
            return;

        Deployment targetDeployment = getDeploymentForEnvironment(environmentId);
        if (targetDeployment.getClass().equals(NullDeployment.class)) {
            targetDeployment = new Deployment(environmentId, latestDeployment, finishedSuccessfully ? latestDeployment : new NullOctopusDate());
            statusMap.add(targetDeployment);
        } else {
            if (targetDeployment.isLatestDeploymentOlderThan(latestDeployment)) {
                targetDeployment.latestDeployment = latestDeployment;
            }
            if (finishedSuccessfully && targetDeployment.isLatestSuccessfulDeploymentOlderThen(latestDeployment)) {
                targetDeployment.latestSuccessfulDeployment = latestDeployment;
            }
        }
    }

    public void addOrUpdate(Deployments moreResults) {
        for (Deployment deployment : moreResults.statusMap) {
            addOrUpdate(deployment.environmentId, deployment.latestDeployment, deployment.latestSuccessfulDeployment);
        }
    }

    public void addOrUpdate(Deployment deployment) {
        if (deployment.getClass().equals(NullDeployment.class))
            return;
        addOrUpdate(deployment.environmentId, deployment.latestDeployment, deployment.latestSuccessfulDeployment);
    }

    private void addOrUpdate(String environmentId, OctopusDate latestDeployment, OctopusDate latestSuccessfulDeployment) {
        Deployment targetDeployment = getDeploymentForEnvironment(environmentId);
        if (targetDeployment.getClass().equals(NullDeployment.class)) {
            targetDeployment = new Deployment(environmentId, latestDeployment, latestSuccessfulDeployment);
            statusMap.add(targetDeployment);
        } else {
            if (targetDeployment.isLatestDeploymentOlderThan(latestDeployment)) {
                targetDeployment.latestDeployment = latestDeployment;
            }
            if (targetDeployment.isLatestSuccessfulDeploymentOlderThen(latestSuccessfulDeployment)) {
                targetDeployment.latestSuccessfulDeployment = latestSuccessfulDeployment;
            }
        }
    }

    public boolean haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment() {
        boolean result = true;
        for (Deployment deployment : statusMap) {
            result = result & deployment.hasHadAtLeastOneSuccessfulDeployment();
        }
        return result;
    }

    public void addEnvironment(String environmentId) {
        addOrUpdate(environmentId, new NullOctopusDate(), new NullOctopusDate());
    }

    public Deployments trimToOnlyHaveMaximumOneChangedEnvironment(Deployments oldDeployments) {
        return trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, false);
    }

    public Deployments trimToOnlyHaveMaximumOneChangedEnvironment(Deployments oldDeployments, Boolean prioritiseSuccessfulDeployments) {
        Deployments newDeployments = new Deployments(oldDeployments);

        final String oldStringRepresentation = oldDeployments.toString();

        if (prioritiseSuccessfulDeployments) {
            for (Deployment deployment : statusMap) {
                Deployment oldDeployment = oldDeployments.getDeploymentForEnvironment(deployment.environmentId);

                if (deployment.isLatestSuccessfulDeploymentNewerThan(oldDeployment.latestSuccessfulDeployment)) {
                    newDeployments.addOrUpdate(deployment);
                    String newStringRepresentation = newDeployments.toString();
                    if (!oldStringRepresentation.equals(newStringRepresentation)) {
                        return newDeployments;
                    }
                }
            }
        }

        for (Deployment deployment : statusMap) {
            newDeployments.addOrUpdate(deployment);
            String newStringRepresentation = newDeployments.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return newDeployments;
            }
        }
        return newDeployments;

    }

    public Deployment getChangedDeployment(Deployments oldDeployments) throws ParseException, NoChangedDeploymentsException {
        Deployments newDeployments = new Deployments(oldDeployments);
        final String oldStringRepresentation = oldDeployments.toString();

        for (Deployment deployment : statusMap) {
            newDeployments.addOrUpdate(deployment);
            String newStringRepresentation = newDeployments.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return deployment;
            }
        }
        throw new NoChangedDeploymentsException(oldDeployments, newDeployments);
    }

    public int size() {
        return statusMap.size();
    }

    public Deployment[] toArray() {
        return statusMap.toArray(new Deployment[0]);
    }
}
