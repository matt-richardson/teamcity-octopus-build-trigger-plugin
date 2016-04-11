package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.NeedToDeleteAndRecreateTrigger;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class ReleaseTest {
    public void can_parse_empty_string_to_null_release() throws NeedToDeleteAndRecreateTrigger {
        Release sut = Release.Parse("");
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }

    public void can_parse_null_string_to_null_release() throws NeedToDeleteAndRecreateTrigger {
        Release sut = Release.Parse((String)null);
        Assert.assertEquals(sut.getClass(), NullRelease.class);
    }

    public void can_parse_valid_string_to_release() throws NeedToDeleteAndRecreateTrigger {
        Release sut = Release.Parse("Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Assert.assertEquals(sut.releaseId, "Releases-91");
        Assert.assertEquals(sut.version, "1.0.0");
        Assert.assertEquals(sut.projectId, "Projects-1");
        Assert.assertEquals(sut.assembledDate, new OctopusDate(2016, 01, 21, 13, 32, 59, 991));
    }

    public void to_string_formats_correctly() throws NeedToDeleteAndRecreateTrigger {
        Release sut = Release.Parse("Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Assert.assertEquals(sut.toString(), "Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
    }

    @Test(expectedExceptions = NeedToDeleteAndRecreateTrigger.class)
    public void parse_throws_exception_if_format_is_different() throws NeedToDeleteAndRecreateTrigger {
        //the format changed (we added another field, projectId), so it needs to be deleted and recreated
        Release.Parse("Releases-91;2016-01-21T13:32:59.991+00:00;1.0.0");
    }

    public void compare_returns_1_when_passed_release_has_newer_assembled_date() throws NeedToDeleteAndRecreateTrigger {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Release release2 = Release.Parse("Releases-2;2016-01-22T13:32:59.991+00:00;1.0.0;Projects-1");
        Assert.assertEquals(release1.compareTo(release2), 1);
    }

    public void compare_returns_0_when_passed_release_has_same_assembled_date() throws NeedToDeleteAndRecreateTrigger {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Release release2 = Release.Parse("Releases-2;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Assert.assertEquals(release1.compareTo(release2), 0);
    }

    public void compare_returns_minus_1_when_passed_release_has_older_assembled_date() throws NeedToDeleteAndRecreateTrigger {
        Release release1 = Release.Parse("Releases-1;2016-01-21T13:32:59.991+00:00;1.0.0;Projects-1");
        Release release2 = Release.Parse("Releases-2;2016-01-20T13:32:59.991+00:00;1.0.0;Projects-1");
        Assert.assertEquals(release1.compareTo(release2), -1);
    }

    public void can_create_from_map() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Id", "Releases-21");
        map.put("Assembled", "2016-01-20T14:32:59.991+00:00");
        map.put("Version", "1.0.3");
        map.put("ProjectId", "Projects-2");
        Release sut = Release.Parse(map);
        Assert.assertEquals(sut.releaseId, "Releases-21");
        Assert.assertEquals(sut.version, "1.0.3");
        Assert.assertEquals(sut.assembledDate, new OctopusDate(2016, 01, 20, 14, 32, 59, 991));
        Assert.assertEquals(sut.projectId, "Projects-2");
    }

    public void equals_returns_false_when_other_object_is_not_a_release() {
        Release sut = new Release("release-1", new OctopusDate(2016, 2, 26), "1.0.0", "the-project-id");
        Assert.assertFalse(sut.equals(new Releases()));
    }

    public void equals_returns_false_when_other_object_is_null() {
        Release sut = new Release("release-1", new OctopusDate(2016, 2, 26), "1.0.0", "the-project-id");
        Assert.assertFalse(sut.equals(null));
    }

    public void equals_returns_true_when_both_objects_are_same() {
        Release sut = new Release("release-1", new OctopusDate(2016, 2, 26), "1.0.0", "the-project-id");
        Release other = new Release("release-1", new OctopusDate(2016, 2, 26), "1.0.0", "the-project-id");
        Assert.assertTrue(sut.equals(other));
    }
}
