package com.mjrichardson.teamCity.buildTriggers.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class Project {
    @JsonIgnore
    public final String progressionApiLink;
    @JsonIgnore
    public final String releasesApiLink;
    public final String id;
    public final String name;
    public final String deploymentProcessLink;

    Project(String id, String name, String releasesApiLink, String progressionApiLink, String deploymentProcessLink) {
        this.id = id;
        this.name = name;
        this.releasesApiLink = releasesApiLink;
        this.progressionApiLink = progressionApiLink;
        this.deploymentProcessLink = deploymentProcessLink;
    }

    public static Project Parse(Map item) {
        String id = item.get("Id").toString();
        String name = item.get("Name").toString();
        String releasesLink = (String) ((Map) item.get("Links")).get("Releases");
        releasesLink = releasesLink.replaceAll("\\{.*\\}", "");
        String progressionApiLink = (String) ((Map) item.get("Links")).get("Progression");
        progressionApiLink = progressionApiLink.replaceAll("\\{.*\\}", "");
        String deploymentProcessLink = (String) ((Map) item.get("Links")).get("DeploymentProcess");
        return new Project(id, name, releasesLink, progressionApiLink, deploymentProcessLink);
    }
}
