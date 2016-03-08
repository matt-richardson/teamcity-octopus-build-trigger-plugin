package com.mjrichardson.teamCity.buildTriggers;

import org.apache.commons.lang.NotImplementedException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;

public class ApiProjectsResponse {
    public Projects projects;
    public String nextLink;

    public ApiProjectsResponse(String projectsResponse) throws ParseException {
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(projectsResponse);

        projects = new Projects();

        List items = (List) response.get("Items");
        for (Object item : items) {
            projects.add(Project.Parse((Map) item));
        }

        Object nextPage = ((Map) response.get("Links")).get("Page.Next");
        if (nextPage != null)
            nextLink = nextPage.toString();
    }

    public boolean isEmpty() {
        return projects.isEmpty();
    }

    public void add(ApiProjectReleasesResponse apiProjectReleasesResponse) {
        throw new NotImplementedException();
    }

    public Project getProject(String projectId) throws ProjectNotFoundException {
        return projects.getProject(projectId);
    }
}
