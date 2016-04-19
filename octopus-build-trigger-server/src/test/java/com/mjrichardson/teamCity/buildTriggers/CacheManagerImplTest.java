package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

@Test
public class CacheManagerImplTest {
    public void can_use_cache_for_all_cache_names() throws URISyntaxException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new CacheManagerImpl(new FakeMetricRegistry());
        for (CacheManager.CacheNames cacheName : CacheManager.CacheNames.values()) {
            if (cacheName == CacheManager.CacheNames.NoCache)
                continue;
            String expectedValue = cacheName.name() + "|cache content";
            URI uri = new URI("http://example.com/api");
            cacheManager.addToCache(cacheName, uri, expectedValue);
            String actualValue = cacheManager.getFromCache(cacheName, uri);
            Assert.assertEquals(actualValue, expectedValue);
        }
    }

    public void can_use_no_cache() throws URISyntaxException, InvalidCacheConfigurationException {
        CacheManager cacheManager = new CacheManagerImpl(new FakeMetricRegistry());
        URI uri = new URI("http://example.com/api");

        cacheManager.addToCache(CacheManager.CacheNames.NoCache, uri, "a random value");
        String actualValue = cacheManager.getFromCache(CacheManager.CacheNames.NoCache, uri);
        Assert.assertEquals(actualValue, null);
    }
}
