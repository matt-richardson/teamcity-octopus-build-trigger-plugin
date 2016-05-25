package com.mjrichardson.teamCity.buildTriggers;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
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
    private BuildTriggerProperties buildTriggerProperties;

    public CacheManagerImpl(MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties, @NotNull EventDispatcher<BuildServerListener> eventDispatcher) {
        this.buildTriggerProperties = buildTriggerProperties;
        this.ehCacheManager = net.sf.ehcache.CacheManager.newInstance();
        eventDispatcher.addListener(new BuildServerAdapter() {
            public void serverShutdown() {
                LOG.debug("Server shutdown initiated - shutting down ehCacheManager");
                ehCacheManager.shutdown();
                LOG.debug("Server shutdown initiated - ehCacheManager shutdown complete");
            }
        });
        this.metricRegistry = metricRegistry;
        this.caches = new HashMap<>();
    }

    private synchronized Ehcache getCache(CacheNames cacheName) throws InvalidCacheConfigurationException {
        if (caches.containsKey(cacheName))
            return caches.get(cacheName);
        Cache rawCache = ehCacheManager.getCache(cacheName.name());
        if (rawCache == null)
            throw new InvalidCacheConfigurationException(cacheName);
        Ehcache instrumentedCache = InstrumentedEhcache.instrument(metricRegistry, rawCache);

        caches.put(cacheName, instrumentedCache);
        return instrumentedCache;
    }

    public String getFromCache(CacheNames cacheName, URI uri, UUID correlationId) throws InvalidCacheConfigurationException {
        if (!buildTriggerProperties.isCacheEnabled()) {
            LOG.debug(String.format("%s: Skipping getting cached response for '%s' from cache '%s' as cache is disabled", correlationId, uri.toString(), cacheName.name()));
            return null;
        }
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
        if (!buildTriggerProperties.isCacheEnabled()) {
            LOG.debug(String.format("%s: Skipping caching response for '%s' in cache '%s' as cache is disabled", correlationId, uri.toString(), cacheName.name()));
            return;
        }
        LOG.debug(String.format("%s: Caching response for '%s' in cache '%s'", correlationId, uri.toString(), cacheName.name()));
        if (cacheName == CacheNames.NoCache)
            return;
        Ehcache cache = getCache(cacheName);
        cache.put(new Element(uri.toString(), body));
    }
}
