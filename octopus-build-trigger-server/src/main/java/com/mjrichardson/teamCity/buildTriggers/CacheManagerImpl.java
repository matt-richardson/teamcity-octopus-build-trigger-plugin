package com.mjrichardson.teamCity.buildTriggers;


import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.util.cache.CacheProvider;
import jetbrains.buildServer.util.cache.SCache;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.UUID;

//todo: allow modification of cache settings via internal properties
public class CacheManagerImpl implements CacheManager {
    @NotNull
    private static final Logger LOG = Logger.getInstance(CacheManagerImpl.class.getName());
    private final CacheProvider cacheProvider;
    private BuildTriggerProperties buildTriggerProperties;

    public CacheManagerImpl(BuildTriggerProperties buildTriggerProperties, CacheProvider cacheProvider) {
        this.buildTriggerProperties = buildTriggerProperties;
        this.cacheProvider = cacheProvider;
    }

    private synchronized SCache<String> getCache(CacheNames cacheName) throws InvalidCacheConfigurationException {
        return cacheProvider.getOrCreateCache(cacheName.name(), String.class);
    }

    public String getFromCache(CacheNames cacheName, URI uri, UUID correlationId) throws InvalidCacheConfigurationException {
        if (!buildTriggerProperties.isCacheEnabled()) {
            LOG.debug(String.format("%s: Skipping getting cached response for '%s' from cache '%s' as cache is disabled", correlationId, uri.toString(), cacheName.name()));
            return null;
        }
        LOG.debug(String.format("%s: Getting cached response for '%s' from cache '%s'", correlationId, uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return null;
        SCache<String> cache = getCache(cacheName);
        String result = cache.read(uri.toString());
        if (result == null) {
            LOG.debug(String.format("%s: Cached response for '%s' was not found in cache '%s'", correlationId, uri.toString(), cacheName.name()));
            return null;
        }
        LOG.debug(String.format("%s: Cached response for '%s' was retrieved from cache '%s'", correlationId, uri.toString(), cacheName.name()));
        return result;
    }

    public void addToCache(CacheNames cacheName, URI uri, String body, UUID correlationId) throws InvalidCacheConfigurationException {
        if (!buildTriggerProperties.isCacheEnabled()) {
            LOG.debug(String.format("%s: Skipping caching response for '%s' in cache '%s' as cache is disabled", correlationId, uri.toString(), cacheName.name()));
            return;
        }
        LOG.debug(String.format("%s: Caching response for '%s' in cache '%s'", correlationId, uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return;
        SCache<String> cache = getCache(cacheName);
        cache.write(uri.toString(), body);
    }
}
