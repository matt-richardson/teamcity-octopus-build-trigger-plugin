package com.mjrichardson.teamCity.buildTriggers.Model;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.Model.Project;
import com.mjrichardson.teamCity.buildTriggers.Model.Projects;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class ProjectsTest {
    public void is_empty_returns_true_when_no_projects() throws Exception {
        Projects projects = new Projects();
        Assert.assertTrue(projects.isEmpty());
    }

    private Project getProject(String id, String releasesApiLink, String progressionApiLink) {
        HashMap<String, Object> linksMap = new HashMap<>();
        linksMap.put("Releases", releasesApiLink);
        linksMap.put("Progression", progressionApiLink);

        HashMap<String, Object> projectMap = new HashMap<>();
        projectMap.put("Id", id);
        projectMap.put("Name", "Project name");
        projectMap.put("Links", linksMap);

        return Project.Parse(projectMap);
    }

    public void is_empty_returns_false_when_has_projects() throws Exception {
        Project project = getProject("Projects-1", "/api/releases", "/api/progression");
        Projects projects = new Projects();
        projects.add(project);
        Assert.assertFalse(projects.isEmpty());
    }

    public void contains_returns_true_when_project_with_specified_id_exists() throws Exception {
        Projects projects = new Projects();
        projects.add(getProject("Projects-1", "/api/releases", "/api/progression"));
        projects.add(getProject("Projects-2", "/api/releases", "/api/progression"));
        Assert.assertTrue(projects.contains("Projects-2"));
    }

    public void contains_returns_false_when_project_with_specified_id_does_not_exist() throws Exception {
        Projects projects = new Projects();
        projects.add(getProject("Projects-1", "/api/releases", "/api/progression"));
        projects.add(getProject("Projects-2", "/api/releases", "/api/progression"));
        Assert.assertFalse(projects.contains("Projects-3"));
    }

    public void get_project_returns_project_when_project_with_specified_id_exists() throws Exception {
        Projects projects = new Projects();
        projects.add(getProject("Projects-1", "/api/releases", "/api/progression"));
        Project project2 = getProject("Projects-2", "/api/releases", "/api/progression");
        projects.add(project2);
        Project result = projects.getProject("Projects-2");
        Assert.assertEquals(result, project2);
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void get_project_throws_exception_when_project_with_specified_id_does_not_exist() throws Exception {
        Projects projects = new Projects();
        projects.add(getProject("Projects-1", "/api/releases", "/api/progression"));
        projects.add(getProject("Projects-2", "/api/releases", "/api/progression"));
        projects.getProject("Projects-3");
    }

    public void add_with_multiple_projects_succeeds() {
        Projects oldProjects = new Projects();
        oldProjects.add(getProject("Projects-1", "/api/releases", "/api/progression"));
        oldProjects.add(getProject("Projects-2", "/api/releases", "/api/progression"));

        Projects newProjects = new Projects();
        newProjects.add(getProject("Projects-3", "/api/releases", "/api/progression"));
        newProjects.add(getProject("Projects-4", "/api/releases", "/api/progression"));

        newProjects.add(oldProjects);

        Assert.assertTrue(newProjects.contains("Projects-1"));
        Assert.assertTrue(newProjects.contains("Projects-2"));
        Assert.assertTrue(newProjects.contains("Projects-3"));
        Assert.assertTrue(newProjects.contains("Projects-4"));
    }
}
