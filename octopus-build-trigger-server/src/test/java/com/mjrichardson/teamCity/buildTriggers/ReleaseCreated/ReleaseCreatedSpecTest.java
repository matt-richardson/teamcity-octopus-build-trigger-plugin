package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ReleaseCreatedSpecTest {
    public void can_create_requestor_string_when_version_not_supplied() {
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", "theproject");
        Assert.assertEquals(sut.getRequestorString(), "Release of project theproject created on theurl");
    }

    public void can_create_requestor_string_when_version_supplied() {
        ReleaseCreatedSpec sut = new ReleaseCreatedSpec("theurl", "theproject", "theversion");
        Assert.assertEquals(sut.getRequestorString(), "Release theversion of project theproject created on theurl");
    }
}
