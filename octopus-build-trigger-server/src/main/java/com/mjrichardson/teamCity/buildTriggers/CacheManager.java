package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidCacheConfigurationException;

import java.net.URI;
import java.util.UUID;

public interface CacheManager {
    String getFromCache(CacheNames cacheName, URI uri, UUID correlationId) throws InvalidCacheConfigurationException;

    void addToCache(CacheNames cacheName, URI uri, String body, UUID correlationId) throws InvalidCacheConfigurationException;

    enum CacheNames {
        ApiRoot,
        ApiTask,
        ApiRelease,
        ApiProjects,
        ApiProjectsReleases,
        ApiProgression,
        ApiMachines,
        ApiDeployments,
        ApiDeploymentProcess,
        GitHubLatestRelease,
        NoCache
    }
}
