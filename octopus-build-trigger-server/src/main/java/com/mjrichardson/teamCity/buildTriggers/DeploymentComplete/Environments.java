package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import jetbrains.buildServer.util.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

public class Environments implements Iterable<Environment>{
    private ArrayList<Environment> statusMap;

    public Environments() {
        this.statusMap = new ArrayList<>();
    }

    public Environments(Environments oldEnvironments) {
        this();
        addOrUpdate(oldEnvironments);
    }

    public Environments(Environment environment) {
        this();
        addOrUpdate(environment);
    }

    public static Environments Parse(String oldStoredData) throws ParseException {
        Environments result = new Environments();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {

            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    final String[] split = pair.split(";");
                    final String environmentId = split[0];
                    final OctopusDate latestDeployment = OctopusDate.Parse(split[1]);
                    final OctopusDate latestSuccessfulDeployment = OctopusDate.Parse(split[2]);
                    result.addOrUpdate(new Environment(environmentId, latestDeployment, latestSuccessfulDeployment));
                }
            }
        }
        return result;
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

    public void addOrUpdate(Environments moreResults) {
        for (Environment environment : moreResults.statusMap) {
            addOrUpdate(environment.environmentId, environment.latestDeployment, environment.latestSuccessfulDeployment, environment.deploymentId, environment.releaseId, environment.version, environment.projectId);
        }
    }

    public void addOrUpdate(Environment environment) {
        if (environment.getClass().equals(NullEnvironment.class))
            return;
        addOrUpdate(environment.environmentId, environment.latestDeployment, environment.latestSuccessfulDeployment,
                environment.deploymentId, environment.releaseId, environment.version, environment.projectId);
    }

    private void addOrUpdate(String environmentId, OctopusDate latestDeployment, OctopusDate latestSuccessfulDeployment, String deploymentId, String releaseId, String version, String projectId) {
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
        targetDeployment.projectId = projectId;
        targetDeployment.deploymentId = deploymentId;
        targetDeployment.version = version;
        targetDeployment.releaseId = releaseId;
    }

    public boolean haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment() {
        boolean result = true;
        for (Environment environment : statusMap) {
            result = result & environment.hasHadAtLeastOneSuccessfulDeployment();
        }
        return result;
    }

    public void addEnvironment(String environmentId) {
        addOrUpdate(environmentId, new NullOctopusDate(), new NullOctopusDate(), null, null, null, null);
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

    @Override
    public Iterator<Environment> iterator() {
        return statusMap.iterator();
    }

    public boolean contains(Environment other) {
        for (int i = 0; i < statusMap.size(); i++) {
            Environment environment = statusMap.get(i);
            if (environment.environmentId.equals(other.environmentId))
                return true;
        }
        return false;
    }

    public Environments removeEnvironmentsNotIn(Environments newEnvironments) {
        Environments deletedEnvironments = new Environments();

        for (int i = 0; i < statusMap.size(); i++) {
            Environment environment = statusMap.get(i);
            if (!newEnvironments.contains(environment))
                deletedEnvironments.addOrUpdate(environment);
        }

        for (int i = 0; i < deletedEnvironments.size(); i++) {
            Environment environment = deletedEnvironments.statusMap.get(i);
            statusMap.remove(environment);
        }
        return deletedEnvironments;}

}
