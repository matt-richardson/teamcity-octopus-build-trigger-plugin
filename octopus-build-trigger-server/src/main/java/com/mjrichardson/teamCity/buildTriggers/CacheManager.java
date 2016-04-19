package com.mjrichardson.teamCity.buildTriggers;

import java.net.URI;

public interface CacheManager {
    String getFromCache(CacheNames cacheName, URI uri) throws InvalidCacheConfigurationException;

    void addToCache(CacheNames cacheName, URI uri, String body) throws InvalidCacheConfigurationException;

    enum CacheNames {
        ApiRoot,
        ApiTask,
        ApiRelease,
        ApiProjects,
        ApiProjectsReleases,
        ApiProgression,
        ApiMachines,
        ApiDeployments,
        NoCache
    }
}
