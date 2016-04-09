package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ReleasesTest {
    public void can_convert_single_release_from_string_and_back_again() throws Exception {
        final String expected = new Release("release-id", new OctopusDate(2016, 3, 3), "1.0.0", "the-project-id").toString();
        Releases releases = Releases.Parse(expected);
        Assert.assertEquals(releases.toString(), expected);
    }

    public void can_convert_multiple_releases_from_string_and_back_again() throws Exception {
        final String expected = String.format("%s|%s",
                new Release("release-2", new OctopusDate(2016, 3, 3), "1.1.0", "Projects-1").toString(),
                new Release("release-1", new OctopusDate(2016, 3, 2), "1.0.0", "Projects-1").toString());
        Releases releases = Releases.Parse(expected);
        Assert.assertEquals(releases.toString(), expected);
    }

    public void can_convert_from_empty_string_and_back_again() throws Exception {
        Releases releases = Releases.Parse("");
        Assert.assertEquals(releases.toString(), "");
    }

    public void no_arg_ctor_returns_empty() throws Exception {
        Releases releases = new Releases();
        Assert.assertEquals(releases.toString(), "");
        Assert.assertTrue(releases.isEmpty());
    }

    public void is_empty_returns_true_when_no_releases() throws Exception {
        Releases releases = Releases.Parse("");
        Assert.assertTrue(releases.isEmpty());
    }

    public void is_empty_returns_false_when_has_releases() throws Exception {
        final String expected = String.format("%s|%s",
                new Release("release-1", new OctopusDate(2016, 3, 2), "1.0.0", "the-project-id").toString(),
                new Release("release-2", new OctopusDate(2016, 3, 3), "1.1.0", "the-project-id").toString());
        Releases releases = Releases.Parse(expected);
        Assert.assertFalse(releases.isEmpty());
    }

    public void passing_single_release_to_ctor_adds_to_collection() throws Exception {
        final Release release = new Release("release-1", new OctopusDate(2016, 3, 2), "1.0.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(release);
        Assert.assertEquals(releases.size(), 1);
        Assert.assertEquals(releases.toString(), release.toString());
    }

    public void get_next_release_returns_next_release_ordered_by_date() throws Exception {
        final Release oldRelease = new Release("release-1", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        final Release currentRelease = new Release("release-2", new OctopusDate(2016, 3, 2), "1.1.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        final String newData = String.format("%s|%s|%s", oldRelease.toString(), currentRelease.toString(), newRelease.toString());
        Releases newReleases = Releases.Parse(newData);

        Release release = newReleases.getNextRelease(currentRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release, newRelease);
    }

    public void get_next_release_returns_old_release_if_no_newer_release() throws Exception {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases newReleases = Releases.Parse(String.format("%s|%s", newRelease.toString(), oldRelease.toString()));

        Release release = newReleases.getNextRelease(newRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release, newRelease);
    }

    public void get_next_release_returns_old_release_if_no_matching_or_newer_release() throws Exception {
        final Release oldRelease = new Release("release-1", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        final Release newRelease = new Release("release-2", new OctopusDate(2016, 3, 2), "1.1.0", "the-project-id");
        final Release nonMatchedRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases newReleases = Releases.Parse(String.format("%s|%s", newRelease.toString(), oldRelease.toString()));

        Release release = newReleases.getNextRelease(nonMatchedRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release, nonMatchedRelease);
    }

    public void get_next_release_returns_oldest_release_if_passed_null_release() throws Exception {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 1), "1.1.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 2), "1.2.0", "the-project-id");
        Releases newReleases = Releases.Parse(String.format("%s|%s", newRelease.toString(), oldRelease.toString()));

        Release release = newReleases.getNextRelease(new NullRelease());
        Assert.assertNotNull(release);
        Assert.assertEquals(release, oldRelease);
    }

    public void to_array_converts_releases_to_array_successfully() {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases releases = Releases.Parse(String.format("%s|%s", oldRelease.toString(), newRelease.toString()));
        Release[] array = releases.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0], oldRelease);
        Assert.assertEquals(array[1], newRelease);
    }

    public void add_with_single_release_adds_item() {
        final Release release = new Release("release-2", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(release);
        Release[] array = releases.toArray();
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], release);
    }

    public void add_with_single_release_does_not_add_duplicate_release() {
        final Release release = new Release("release-2", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(release);
        releases.add(release);
        Release[] array = releases.toArray();
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], release);
    }

    public void add_with_single_release_does_not_add_null_release() {
        Releases releases = new Releases();
        releases.add(new NullRelease());
        Assert.assertTrue(releases.isEmpty());
    }

    public void add_with_multiple_releases_adds_items() {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(oldRelease);
        releases.add(newRelease);
        Releases sut = new Releases();
        sut.add(releases);
        Release[] array = sut.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0], oldRelease);
        Assert.assertEquals(array[1], newRelease);
    }

    public void contains_returns_false_if_no_match() {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 2), "1.1.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(oldRelease);
        releases.add(newRelease);
        Assert.assertFalse(releases.contains(new Release("release-1", new OctopusDate(2016, 3, 1), "1.0.0", "the-project-id")));
    }

    public void contains_returns_true_if_match() {
        final Release oldRelease = new Release("release-2", new OctopusDate(2016, 3, 2), "1.1.0", "the-project-id");
        final Release newRelease = new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id");
        Releases releases = new Releases();
        releases.add(oldRelease);
        releases.add(newRelease);
        Assert.assertTrue(releases.contains(new Release("release-3", new OctopusDate(2016, 3, 3), "1.2.0", "the-project-id")));
    }
}
