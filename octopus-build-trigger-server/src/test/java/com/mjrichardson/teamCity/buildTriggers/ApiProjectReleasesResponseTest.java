package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApiProjectReleasesResponseTest {
    public void can_parse_valid_response_without_next_link() throws ParseException, IOException {
        String json = ResourceHandler.getResource("api/projects/Projects-28/releases");
        ApiProjectReleasesResponse sut = new ApiProjectReleasesResponse(json);
        Assert.assertEquals(sut.nextLink, null);
        Assert.assertEquals(sut.releases.size(), 2);
        Release[] releases = sut.releases.toArray();
        Assert.assertEquals(releases[0].id, "Releases-70");
        Assert.assertEquals(releases[0].version, "0.0.2");
        Assert.assertEquals(releases[1].id, "Releases-69");
        Assert.assertEquals(releases[1].version, "0.0.1");
    }

    public void can_parse_valid_response_with_next_link() throws IOException, ParseException {
        String json = ResourceHandler.getResource("api/projects/Projects-103/releases");
        ApiProjectReleasesResponse sut = new ApiProjectReleasesResponse(json);
        Assert.assertEquals(sut.nextLink, "/api/projects/Projects-103/releases?skip=30");
        Assert.assertEquals(sut.releases.size(), 30);
    }


}

