package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ReleasesProviderException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.ProjectNotFoundException;

import java.text.ParseException;
import java.util.UUID;

public interface ReleasesProvider {
    Releases getReleases(String octopusProject, Release oldRelease, UUID correlationId) throws ReleasesProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException;
}
