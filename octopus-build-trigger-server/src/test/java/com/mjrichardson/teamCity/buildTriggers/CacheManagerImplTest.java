package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeEventDispatcher;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Test
public class CacheManagerImplTest {
    public void can_use_cache_for_all_cache_names() throws URISyntaxException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new CacheManagerImpl(new FakeMetricRegistry(), new FakeBuildTriggerProperties(), new FakeEventDispatcher());
        for (CacheManager.CacheNames cacheName : CacheManager.CacheNames.values()) {
            if (cacheName == CacheManager.CacheNames.NoCache)
                continue;
            String expectedValue = cacheName.name() + "|cache content";
            URI uri = new URI("http://example.com/api");
            UUID correlationId = UUID.randomUUID();
            cacheManager.addToCache(cacheName, uri, expectedValue, correlationId);
            String actualValue = cacheManager.getFromCache(cacheName, uri, correlationId);
            Assert.assertEquals(actualValue, expectedValue);
        }
    }

    public void can_use_no_cache() throws URISyntaxException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new CacheManagerImpl(new FakeMetricRegistry(), new FakeBuildTriggerProperties(), new FakeEventDispatcher());
        URI uri = new URI("http://example.com/api");

        UUID correlationId = UUID.randomUUID();
        cacheManager.addToCache(CacheManager.CacheNames.NoCache, uri, "a random value", correlationId);
        String actualValue = cacheManager.getFromCache(CacheManager.CacheNames.NoCache, uri, correlationId);
        Assert.assertEquals(actualValue, null);
    }
}
