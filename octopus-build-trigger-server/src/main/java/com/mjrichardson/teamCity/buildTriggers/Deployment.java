package com.mjrichardson.teamCity.buildTriggers;

import java.util.Map;

public class Deployment {
    public final String deploymentId;
    public final String environmentId;
    public final String projectId;
    public final String releaseId;
    public final OctopusDate createdDate;
    public final String taskLink;
    public final String releaseLink;


    public Deployment(String deploymentId, String environmentId, OctopusDate createdDate, String taskLink, String releaseId, String projectId, String releaseLink) {
        this.deploymentId = deploymentId;
        this.environmentId = environmentId;
        this.createdDate = createdDate;
        this.taskLink = taskLink;
        this.releaseId = releaseId;
        this.projectId = projectId;
        this.releaseLink = releaseLink;
    }

    public static Deployment Parse(Map item) {
        String deploymentId = item.get("Id").toString();
        String environmentId = item.get("EnvironmentId").toString();
        OctopusDate createdDate = OctopusDate.Parse(item.get("Created").toString());
        String releaseId = item.get("ReleaseId").toString();
        String projectId = item.get("ProjectId").toString();

        Map links = (Map) (item.get("Links"));
        String taskLink = links.get("Task").toString();
        String releaseLink = links.get("Release").toString();

        return new Deployment(deploymentId, environmentId, createdDate, taskLink, releaseId, projectId, releaseLink);
    }
}
