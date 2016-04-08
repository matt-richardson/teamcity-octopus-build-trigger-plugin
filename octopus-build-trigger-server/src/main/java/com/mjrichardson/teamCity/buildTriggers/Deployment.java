package com.mjrichardson.teamCity.buildTriggers;

import java.util.Map;

public class Deployment {
    public final String deploymentId;
    public final String environmentId;
    public final OctopusDate createdDate;
    public String taskLink;

    Deployment(String deploymentId, String environmentId, OctopusDate createdDate, String taskLink) {
        this.deploymentId = deploymentId;
        this.environmentId = environmentId;
        this.createdDate = createdDate;
        this.taskLink = taskLink;
    }

    public static Deployment Parse(Map item) {
        String deploymentId = item.get("Id").toString();
        String environmentId = item.get("EnvironmentId").toString();
        OctopusDate createdDate = OctopusDate.Parse(item.get("Created").toString());

        Map links = (Map) (item.get("Links"));
        String taskLink = links.get("Task").toString();

        return new Deployment(deploymentId, environmentId, createdDate, taskLink);
    }
}
