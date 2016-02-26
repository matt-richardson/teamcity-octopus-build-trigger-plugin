package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class ReleaseTest {
    public void can_parse_empty_string_to_null_release() {
        Release sut = Release.Parse("");
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }

    public void can_parse_null_string_to_null_release() {
        Release sut = Release.Parse(null);
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }

    public void can_parse_valid_string_to_release() {
        Release sut = Release.Parse("Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0");
        Assert.assertEquals(sut.id, "Releases-91");
        Assert.assertEquals(sut.version, "1.0.0");
        Assert.assertEquals(sut.assembledDate.toString(), "2016-01-21T13:32:59.991+00:00");
    }

    public void to_string_formats_correctly() {
        Release sut = Release.Parse("Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0");
        Assert.assertEquals(sut.toString(), "Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0");
    }

    public void compare_returns_1_when_passed_release_has_newer_assembled_date() {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0");
        Release release2 = Release.Parse("Releases-2;2016-01-22T13:32:59.991+00:00;1.0.0");
        Assert.assertEquals(release1.compareTo(release2), 1);
    }

    public void compare_returns_0_when_passed_release_has_same_assembled_date() {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0");
        Release release2 = Release.Parse("Releases-2;2016-01-21T13:32:59.991+00:00;1.0.0");
        Assert.assertEquals(release1.compareTo(release2), 0);
    }

    public void compare_returns_minus_1_when_passed_release_has_older_assembled_date() {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0");
        Release release2 = Release.Parse("Releases-2;2016-01-20T13:32:59.991+00:00;1.0.0");
        Assert.assertEquals(release1.compareTo(release2), -1);
    }

    public void can_create_from_map() {
        HashMap<String,String> map = new HashMap<>();
        map.put("Id", "Releases-21");
        map.put("Assembled", "2016-01-20T14:32:59.991+00:00");
        map.put("Version", "1.0.3");
        Release sut = new Release(map);
        Assert.assertEquals(sut.id, "Releases-21");
        Assert.assertEquals(sut.version, "1.0.3");
        Assert.assertEquals(sut.assembledDate.toString(), "2016-01-20T14:32:59.991+00:00");
    }
}
