package com.mjrichardson.teamCity.buildTriggers;


import com.intellij.openapi.diagnostic.Logger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public class CacheManagerImpl implements CacheManager {
    @NotNull
    private static final Logger LOG = Logger.getInstance(CacheManagerImpl.class.getName());
    private net.sf.ehcache.CacheManager ehCacheManager;

    public CacheManagerImpl() {
        ehCacheManager = net.sf.ehcache.CacheManager.newInstance();
    }

    public String getFromCache(CacheNames cacheName, URI uri) {
        LOG.debug(String.format("Getting cached response for '%s' from cache '%s'", uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return null;
        Cache cache = ehCacheManager.getCache(cacheName.name());
        Element result = cache.get(uri.toString());
        if (result == null) {
            LOG.debug(String.format("Cached response for '%s' was not found in cache '%s'", uri.toString(), cacheName.name()));
            return null;
        }
        LOG.debug(String.format("Cached response for '%s' was retrieved from cache '%s'", uri.toString(), cacheName.name()));
        return result.getObjectValue().toString();
    }

    public void addToCache(CacheNames cacheName, URI uri, String body) throws InvalidCacheConfigurationException {
        //todo: check property for cache enabled
        LOG.debug(String.format("Caching response for '%s' in cache '%s'", uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return;
        Cache cache = ehCacheManager.getCache(cacheName.name());
        if (cache == null)
            throw new InvalidCacheConfigurationException(cacheName);
        cache.put(new Element(uri.toString(), body));
    }
}
