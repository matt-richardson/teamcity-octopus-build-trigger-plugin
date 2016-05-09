package com.mjrichardson.teamCity.buildTriggers;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;
import com.intellij.openapi.diagnostic.Logger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

//todo: allow modification of cache settings via internal properties
public class CacheManagerImpl implements CacheManager {
    @NotNull
    private static final Logger LOG = Logger.getInstance(CacheManagerImpl.class.getName());
    private final MetricRegistry metricRegistry;
    private net.sf.ehcache.CacheManager ehCacheManager;
    private HashMap<CacheNames, Ehcache> caches;

    public CacheManagerImpl(MetricRegistry metricRegistry) {
        ehCacheManager = net.sf.ehcache.CacheManager.newInstance();
        this.metricRegistry = metricRegistry;
        caches = new HashMap<>();
    }

    private synchronized Ehcache getCache(CacheNames cacheName) throws InvalidCacheConfigurationException {
        if (caches.containsKey(cacheName))
            return caches.get(cacheName);
        Cache rawCache = ehCacheManager.getCache(cacheName.name());
        if (rawCache == null)
            throw new InvalidCacheConfigurationException(cacheName);
        Ehcache instrument = InstrumentedEhcache.instrument(metricRegistry, rawCache);
        caches.put(cacheName, instrument);
        return instrument;
    }

    public String getFromCache(CacheNames cacheName, URI uri, UUID correlationId) throws InvalidCacheConfigurationException {
        LOG.debug(String.format("%s: Getting cached response for '%s' from cache '%s'", correlationId, uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return null;
        Ehcache cache = getCache(cacheName);
        Element result = cache.get(uri.toString());
        if (result == null) {
            LOG.debug(String.format("%s: Cached response for '%s' was not found in cache '%s'", correlationId, uri.toString(), cacheName.name()));
            return null;
        }
        LOG.debug(String.format("%s: Cached response for '%s' was retrieved from cache '%s'", correlationId, uri.toString(), cacheName.name()));
        return result.getObjectValue().toString();
    }

    public void addToCache(CacheNames cacheName, URI uri, String body, UUID correlationId) throws InvalidCacheConfigurationException {
        //todo: check property for cache enabled
        LOG.debug(String.format("%s: Caching response for '%s' in cache '%s'", correlationId, uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return;
        Ehcache cache = getCache(cacheName);
        cache.put(new Element(uri.toString(), body));
    }
}
