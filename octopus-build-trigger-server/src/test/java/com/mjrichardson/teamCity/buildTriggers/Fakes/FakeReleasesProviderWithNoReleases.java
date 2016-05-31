package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Releases;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.ReleasesProvider;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ReleasesProviderException;

import java.text.ParseException;
import java.util.UUID;

public class FakeReleasesProviderWithNoReleases implements ReleasesProvider {
    @Override
    public Releases getReleases(String octopusProject, Release oldRelease, UUID correlationId) throws ReleasesProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        return new Releases();
    }
}
