package com.mjrichardson.teamCity.buildTriggers;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public class GitHubApiReleaseResponseTest {
    public void can_parse_response() throws IOException, ParseException {

        final String resourceName = "/responses/api.github.com/repos/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest.json";
        InputStream resource = ResourceHandler.class.getResourceAsStream(resourceName);
        final String json = IOUtils.toString(resource);

        GitHubApiReleaseResponse sut = new GitHubApiReleaseResponse(json);

        Assert.assertEquals(sut.tagName, "2.2.0+build.129");
        Assert.assertEquals(sut.htmlUrl, "https://github.com/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/tag/2.2.0%2Bbuild.129");
    }
}
