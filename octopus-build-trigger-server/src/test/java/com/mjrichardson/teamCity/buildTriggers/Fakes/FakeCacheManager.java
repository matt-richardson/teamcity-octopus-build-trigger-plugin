package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.CacheManager;

import java.net.URI;
import java.util.HashMap;

public class FakeCacheManager implements CacheManager {
    private HashMap<String, String> cache = new HashMap<>();

    @Override
    public String getFromCache(CacheNames cacheName, URI uri) {
        String key = cacheName.name() + "|" + uri.toString();
        if (cache.containsKey(key))
            return cache.get(key);
        return null;
    }

    @Override
    public void addToCache(CacheNames cacheName, URI uri, String body) {
        String key = cacheName.name() + "|" + uri.toString();
        cache.put(key, body);
    }
}
