package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class ProjectTest {
    public void can_parse_valid_map_into_project() throws Exception {
        HashMap<String, Object> linksMap = new HashMap<>();
        linksMap.put("Releases", "/api/Projects/project-24/releases");
        linksMap.put("Progression", "/api/Progression/project-24");

        HashMap<String, Object> projectMap = new HashMap<>();
        projectMap.put("Id", "Projects-24");
        projectMap.put("Links", linksMap);

        Project result = Project.Parse(projectMap);

        Assert.assertEquals(result.id, "Projects-24");
        Assert.assertEquals(result.progressionApiLink, "/api/Progression/project-24");
        Assert.assertEquals(result.releasesApiLink, "/api/Projects/project-24/releases");
    }
}
