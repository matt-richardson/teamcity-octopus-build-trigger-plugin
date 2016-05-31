package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Releases;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ReleasesProviderException;

import java.text.ParseException;
import java.util.UUID;

public class FakeReleasesProviderWithOneRelease implements ReleasesProvider {
    @Override
    public Releases getReleases(String octopusProject, Release oldRelease, UUID correlationId) throws ReleasesProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        Release release = new Release("release-1", new OctopusDate(2016, 3, 1), "1.0.0", octopusProject);
        Releases releases = new Releases();
        releases.add(release);
        return releases;
    }
}
