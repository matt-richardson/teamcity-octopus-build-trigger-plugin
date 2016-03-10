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

public class Environments {
    private ArrayList<Environment> statusMap;

    public Environments() {
        this.statusMap = new ArrayList<>();
    }

    //todo: this ctor should move to a Parse method.
    public Environments(String oldStoredData) throws ParseException {
        this();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {

            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    final String[] split = pair.split(";");
                    final String environmentId = split[0];
                    final OctopusDate latestDeployment = OctopusDate.Parse(split[1]);
                    final OctopusDate latestSuccessfulDeployment = OctopusDate.Parse(split[2]);
                    statusMap.add(new Environment(environmentId, latestDeployment, latestSuccessfulDeployment));
                }
            }
        }
    }

    public Environments(Environments oldEnvironments) {
        this();
        addOrUpdate(oldEnvironments);
    }

    public Environments(Environment environment) {
        this();
        addOrUpdate(environment);
    }

    @Override
    public String toString() {
        String result = "";

        for (Environment environment : statusMap) {
            result = String.format("%s%s|", result, environment.toString());
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

        final Environments other = (Environments) obj;

        if (this.statusMap.size() != other.statusMap.size()) {
            return false;
        }

        for (Environment environment : statusMap) {
            Boolean found = false;
            for (Environment otherDeployment : other.statusMap) {
                if (otherDeployment.environmentId.equals(environment.environmentId)) {
                    found = true;
                    if (!otherDeployment.latestDeployment.equals(environment.latestDeployment)) {
                        return false;
                    }
                    if (!otherDeployment.latestSuccessfulDeployment.equals(environment.latestSuccessfulDeployment)) {
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

    public Environment getEnvironment(String environmentId) {
        for (Environment environment : statusMap) {
            if (environment.environmentId.equals(environmentId)) {
                return environment;
            }
        }
        return new NullEnvironment();
    }

    public void addOrUpdate(String environmentId, OctopusDate latestDeployment, Boolean isCompleted, Boolean finishedSuccessfully) {
        if (!isCompleted)
            return;

        Environment targetDeployment = getEnvironment(environmentId);
        if (targetDeployment.getClass().equals(NullEnvironment.class)) {
            targetDeployment = new Environment(environmentId, latestDeployment, finishedSuccessfully ? latestDeployment : new NullOctopusDate());
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

    public void addOrUpdate(Environments moreResults) {
        for (Environment environment : moreResults.statusMap) {
            addOrUpdate(environment.environmentId, environment.latestDeployment, environment.latestSuccessfulDeployment);
        }
    }

    public void addOrUpdate(Environment environment) {
        if (environment.getClass().equals(NullEnvironment.class))
            return;
        addOrUpdate(environment.environmentId, environment.latestDeployment, environment.latestSuccessfulDeployment);
    }

    private void addOrUpdate(String environmentId, OctopusDate latestDeployment, OctopusDate latestSuccessfulDeployment) {
        Environment targetDeployment = getEnvironment(environmentId);
        if (targetDeployment.getClass().equals(NullEnvironment.class)) {
            targetDeployment = new Environment(environmentId, latestDeployment, latestSuccessfulDeployment);
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
        for (Environment environment : statusMap) {
            result = result & environment.hasHadAtLeastOneSuccessfulDeployment();
        }
        return result;
    }

    public void addEnvironment(String environmentId) {
        addOrUpdate(environmentId, new NullOctopusDate(), new NullOctopusDate());
    }

    public Environments trimToOnlyHaveMaximumOneChangedEnvironment(Environments oldEnvironments) {
        return trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, false);
    }

    public Environments trimToOnlyHaveMaximumOneChangedEnvironment(Environments oldEnvironments, Boolean prioritiseSuccessfulDeployments) {
        Environments newEnvironments = new Environments(oldEnvironments);

        final String oldStringRepresentation = oldEnvironments.toString();

        if (prioritiseSuccessfulDeployments) {
            for (Environment environment : statusMap) {
                Environment oldDeployment = oldEnvironments.getEnvironment(environment.environmentId);

                if (environment.isLatestSuccessfulDeploymentNewerThan(oldDeployment.latestSuccessfulDeployment)) {
                    newEnvironments.addOrUpdate(environment);
                    String newStringRepresentation = newEnvironments.toString();
                    if (!oldStringRepresentation.equals(newStringRepresentation)) {
                        return newEnvironments;
                    }
                }
            }
        }

        for (Environment environment : statusMap) {
            newEnvironments.addOrUpdate(environment);
            String newStringRepresentation = newEnvironments.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return newEnvironments;
            }
        }
        return newEnvironments;

    }

    public Environment getChangedDeployment(Environments oldEnvironments) throws ParseException, NoChangedEnvironmentsException {
        Environments newEnvironments = new Environments(oldEnvironments);
        final String oldStringRepresentation = oldEnvironments.toString();

        for (Environment environment : statusMap) {
            newEnvironments.addOrUpdate(environment);
            String newStringRepresentation = newEnvironments.toString();
            if (!oldStringRepresentation.equals(newStringRepresentation)) {
                return environment;
            }
        }
        throw new NoChangedEnvironmentsException(oldEnvironments, newEnvironments);
    }

    public int size() {
        return statusMap.size();
    }

    public Environment[] toArray() {
        return statusMap.toArray(new Environment[0]);
    }
}
