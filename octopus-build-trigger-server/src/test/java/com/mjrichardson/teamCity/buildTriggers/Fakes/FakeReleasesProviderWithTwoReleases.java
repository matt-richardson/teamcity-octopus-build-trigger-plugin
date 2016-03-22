package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Releases;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProvider;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProviderException;

import java.text.ParseException;

public class FakeReleasesProviderWithTwoReleases implements ReleasesProvider {
    @Override
    public Releases getReleases(String octopusProject, Release oldRelease) throws ReleasesProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Releases releases = new Releases();
        releases.add(new Release("release-1", new OctopusDate(2016, 3, 1), "1.0.0", "Project-1"));
        releases.add(new Release("release-2", new OctopusDate(2016, 3, 2), "1.1.0", "Project-1"));
        return releases;
    }
}
