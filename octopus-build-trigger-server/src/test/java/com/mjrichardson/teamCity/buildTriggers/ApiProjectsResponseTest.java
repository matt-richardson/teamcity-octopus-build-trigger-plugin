package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApiProjectsResponseTest {
    public void can_parse_projects_response() throws IOException, ParseException {
        final String json = ResourceHandler.getResource("api/projects");
        ApiProjectsResponse sut = new ApiProjectsResponse(json);

        Assert.assertFalse(sut.projects.isEmpty());
        Assert.assertEquals(sut.projects.size(), 30);
        Project[] array = sut.projects.toArray();
        Assert.assertEquals(array[0].id, "Projects-143");
        Assert.assertEquals(array[0].progressionApiLink, "/api/progression/Projects-24");
        Assert.assertEquals(array[0].releasesApiLink, "/api/projects/Projects-24/releases");
        Assert.assertEquals(sut.nextLink, "/api/projects?skip=30");
    }

    public void can_parse_projects_response_with_no_projects() throws IOException, ParseException {
        final String json = ResourceHandler.getResource("api/projects-with-no-items");
        ApiProjectsResponse sut = new ApiProjectsResponse(json);

        Assert.assertEquals(sut.projects.size(), 0);
        Assert.assertTrue(sut.projects.isEmpty());
    }
}
