package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ReleaseCreatedSpecTest {
    public void can_create_requestor_string_when_version_not_supplied() {
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", "theprojectid");
        Assert.assertEquals(sut.getRequestorString(), "Release of project theprojectid created on theurl");
    }

    public void can_create_requestor_string_when_version_supplied() {
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", "theprojectid", "theversion", "thereleaseid");
        Assert.assertEquals(sut.getRequestorString(), "Release theversion of project theprojectid created on theurl");
    }

    public void to_string_converts_correctly() {
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", "theprojectid", "theversion", "thereleaseid");
        String result = sut.toString();
        Assert.assertEquals(result, "{ url: 'theurl', projectId: 'theprojectid', version: 'theversion', releaseId: 'thereleaseid' }");
    }
}
