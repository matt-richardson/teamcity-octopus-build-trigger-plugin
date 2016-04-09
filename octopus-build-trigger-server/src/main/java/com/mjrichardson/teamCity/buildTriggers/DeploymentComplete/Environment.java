package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.*;

import java.util.Map;

public class Environment {
    public final String environmentId;
    public OctopusDate latestDeployment;
    public OctopusDate latestSuccessfulDeployment;
    String releaseId;
    String deploymentId;
    String version;
    String projectId;

    public Environment(String environmentId, OctopusDate latestDeployment, OctopusDate latestSuccessfulDeployment, String releaseId, String deploymentId, String version, String projectId) {
        this.environmentId = environmentId;
        this.latestDeployment = latestDeployment;
        this.latestSuccessfulDeployment = latestSuccessfulDeployment;
        this.releaseId = releaseId;
        this.deploymentId = deploymentId;
        this.version = version;
        this.projectId = projectId;
    }

    public boolean isLatestDeploymentOlderThan(OctopusDate compareDate) {
        return this.latestDeployment.compareTo(compareDate) < 0;
    }

    public boolean isLatestSuccessfulDeploymentOlderThen(OctopusDate compareDate) {
        return this.latestSuccessfulDeployment.compareTo(compareDate) < 0;
    }

    public boolean wasLatestDeploymentSuccessful() {
        return this.latestSuccessfulDeployment.compareTo(this.latestDeployment) == 0;
    }

    public boolean hasHadAtLeastOneSuccessfulDeployment() {
        return this.latestSuccessfulDeployment.compareTo(new NullOctopusDate()) > 0;
    }

    public void update(OctopusDate latestDeployment, Boolean finishedSuccessfully) {
        if (isLatestDeploymentOlderThan(latestDeployment)) {
            this.latestDeployment = latestDeployment;
        }
        if (finishedSuccessfully && isLatestSuccessfulDeploymentOlderThen(latestDeployment)) {
            latestSuccessfulDeployment = latestDeployment;
        }
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s;%s;%s", environmentId, latestDeployment, latestSuccessfulDeployment, releaseId, deploymentId, version, projectId);
    }

    public static Environment Parse(String toStringRepresentation) {
        final String[] split = toStringRepresentation.split(";");
        final String environmentId = split[0];
        final OctopusDate latestDeployment = OctopusDate.Parse(split[1]);
        final OctopusDate latestSuccessfulDeployment = OctopusDate.Parse(split[2]);
        String releaseId = null;
        String deploymentId = null;
        String version = null;
        String projectId = null;

        if (split.length > 3) {
            releaseId = split[3];
            deploymentId = split[4];
            version = split[5];
            projectId = split[6];
        }

        return new Environment(environmentId, latestDeployment, latestSuccessfulDeployment, releaseId, deploymentId, version, projectId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != Environment.class && obj.getClass() != NullEnvironment.class)
            return false;
        return obj.toString().equals(toString());
    }

    public boolean isLatestSuccessfulDeploymentNewerThan(OctopusDate compareDate) {
        return this.latestSuccessfulDeployment.compareTo(compareDate) > 0;
    }

    public static Environment Parse(Map map) {
        Boolean isCompleted = Boolean.parseBoolean(map.get("IsCompleted").toString());
        if (!isCompleted)
            return new NullEnvironment();

        OctopusDate createdDate = OctopusDate.Parse(map.get("Created").toString());
        Boolean isSuccessful = map.get("State").toString().equals("Success");
        String environmentId = map.get("EnvironmentId").toString();
        String releaseId = map.get("ReleaseId").toString();
        String deploymentId = map.get("DeploymentId").toString();
        String version = map.get("ReleaseVersion").toString();
        String projectId = map.get("ProjectId").toString();

        if (isSuccessful)
            return new Environment(environmentId, createdDate, createdDate, releaseId, deploymentId, version, projectId);
        return new Environment(environmentId, createdDate, new NullOctopusDate(), releaseId, deploymentId, version, projectId);
    }

    public static Environment CreateFrom(Deployment deployment, ApiTaskResponse task, ApiReleaseResponse release) {
        OctopusDate createdDate = deployment.createdDate;
        Boolean isSuccessful = task.finishedSuccessfully;
        String environmentId = deployment.environmentId;
        String releaseId = deployment.releaseId;
        String deploymentId = deployment.deploymentId;
        String version = release.version;
        String projectId = deployment.projectId;

        return new Environment(environmentId,
                createdDate,
                isSuccessful ? createdDate : new NullOctopusDate(),
                releaseId,
                deploymentId,
                version,
                projectId);
    }
}
