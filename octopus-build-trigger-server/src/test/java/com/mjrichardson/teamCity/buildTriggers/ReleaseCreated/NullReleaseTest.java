package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullReleaseTest {
    public void null_release_sets_fields_to_empty() {
        Release sut = new NullRelease();
        Assert.assertEquals(sut.id, "");
        Assert.assertEquals(sut.assembledDate.getClass(), NullOctopusDate.class);
        Assert.assertEquals(sut.version, "");
    }

    public void can_parse_to_null_release() {
        Release sut = Release.Parse(new NullRelease().toString());
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }
}
