package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.NeedToDeleteAndRecreateTriggerException;
import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullReleaseTest {
    public void null_release_sets_fields_to_empty() {
        Release sut = new NullRelease();
        Assert.assertEquals(sut.releaseId, "");
        Assert.assertEquals(sut.assembledDate.getClass(), NullOctopusDate.class);
        Assert.assertEquals(sut.version, "");
    }

    public void can_parse_to_null_release() throws NeedToDeleteAndRecreateTriggerException {
        Release sut = Release.Parse(new NullRelease().toString());
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }
}
