package com.mjrichardson.teamCity.buildTriggers;

import java.util.Map;

public class Project {
    public final String progressionApiLink;
    public final String releasesApiLink;
    public final String id;

    Project(String id, String releasesApiLink, String progressionApiLink) {
        this.id = id;
        this.releasesApiLink = releasesApiLink;
        this.progressionApiLink = progressionApiLink;
    }

    public static Project Parse(Map item) {
        String id = item.get("Id").toString();
        String releasesLink = (String) ((Map) item.get("Links")).get("Releases");
        releasesLink = releasesLink.replaceAll("\\{.*\\}", "");
        String progressionApiLink = (String) ((Map) item.get("Links")).get("Progression");
        progressionApiLink = progressionApiLink.replaceAll("\\{.*\\}", "");
        return new Project(id, releasesLink, progressionApiLink);
    }
}
