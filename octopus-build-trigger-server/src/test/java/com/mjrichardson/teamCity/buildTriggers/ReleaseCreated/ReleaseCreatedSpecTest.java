package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ReleaseCreatedSpecTest {
    public void can_create_requestor_string_when_version_not_supplied() {
        Release release = new Release("the-release-id", new OctopusDate(2016, 4, 9), null, "the-project-id");
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", release);
        Assert.assertEquals(sut.getRequestorString(), "Release of project the-project-id created on theurl");
    }

    public void can_create_requestor_string_when_version_supplied() {
        Release release = new Release("the-release-id", new OctopusDate(2016, 4, 9), "the-version", "the-project-id");
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("the-url", release);
        Assert.assertEquals(sut.getRequestorString(), "Release the-version of project the-project-id created on the-url");
    }

    public void to_string_converts_correctly() {
        Release release = new Release("the-release-id", new OctopusDate(2016, 4, 9), "the-version", "the-project-id");
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("the-url", release);
        String result = sut.toString();
        Assert.assertEquals(result, "{ url: 'the-url', projectId: 'the-project-id', version: 'the-version', releaseId: 'the-release-id' }");
    }
}
