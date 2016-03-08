package com.mjrichardson.teamCity.buildTriggers;


import java.util.ArrayList;

public class Projects {
    private ArrayList<Project> statusMap;

    public Projects() {
        this.statusMap = new ArrayList<>();
    }

    public boolean contains(String projectId) {
        for (Project project : statusMap) {
            if (project.id.equals(projectId))
                return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return statusMap.size() == 0;
    }

    public Project getProject(String projectId) throws ProjectNotFoundException {
        for (Project project : statusMap) {
            if (project.id.equals(projectId))
                return project;
        }
        throw new ProjectNotFoundException(projectId);
    }

    public void add(Project project) {
        if (!contains(project.id))
            statusMap.add(project);
    }
}
