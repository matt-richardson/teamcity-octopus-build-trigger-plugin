package com.mjrichardson.teamCity.buildTriggers;

import java.util.Map;

public class Deployment {
    final String id;
    public final String environmentId;
    public final OctopusDate createdDate;
    public String taskLink;

    Deployment(String id, String environmentId, OctopusDate createdDate, String taskLink) {
        this.id = id;
        this.environmentId = environmentId;
        this.createdDate = createdDate;
        this.taskLink = taskLink;
    }

    public static Deployment Parse(Map item) {
        String id = item.get("Id").toString();
        String environmentId = item.get("EnvironmentId").toString();
        OctopusDate createdDate = OctopusDate.Parse(item.get("Created").toString());
        Map links = (Map) (item.get("Links"));
        String taskLink = links.get("Task").toString();

        return new Deployment(id, environmentId, createdDate, taskLink);
    }
}
