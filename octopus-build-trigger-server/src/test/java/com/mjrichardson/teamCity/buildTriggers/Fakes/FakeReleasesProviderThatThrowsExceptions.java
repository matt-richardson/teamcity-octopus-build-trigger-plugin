package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Releases;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProvider;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProviderException;

import java.text.ParseException;
import java.util.UUID;

public class FakeReleasesProviderThatThrowsExceptions extends FakeReleasesProviderWithNoReleases implements ReleasesProvider {
    @Override
    public Releases getReleases(String octopusProject, Release oldRelease, UUID correlationId) throws ReleasesProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        throw new ProjectNotFoundException("project not found");
    }
}
