package com.mjrichardson.teamCity.buildTriggers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class Project {
    @JsonIgnore
    public final String progressionApiLink;
    @JsonIgnore
    public final String releasesApiLink;
    public final String id;
    public final String name;

    Project(String id, String name, String releasesApiLink, String progressionApiLink) {
        this.id = id;
        this.name = name;
        this.releasesApiLink = releasesApiLink;
        this.progressionApiLink = progressionApiLink;
    }

    public static Project Parse(Map item) {
        String id = item.get("Id").toString();
        String name = item.get("Name").toString();
        String releasesLink = (String) ((Map) item.get("Links")).get("Releases");
        releasesLink = releasesLink.replaceAll("\\{.*\\}", "");
        String progressionApiLink = (String) ((Map) item.get("Links")).get("Progression");
        progressionApiLink = progressionApiLink.replaceAll("\\{.*\\}", "");
        return new Project(id, name, releasesLink, progressionApiLink);
    }
}
