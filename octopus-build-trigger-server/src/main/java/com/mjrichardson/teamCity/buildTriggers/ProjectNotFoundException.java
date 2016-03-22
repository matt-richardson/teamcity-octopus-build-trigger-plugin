package com.mjrichardson.teamCity.buildTriggers;

public class ProjectNotFoundException extends Exception {
    public final String projectIdOrName;

    public ProjectNotFoundException(String projectIdOrName) {
        super("Unable to find project " + projectIdOrName);
        this.projectIdOrName = projectIdOrName;
    }
}

