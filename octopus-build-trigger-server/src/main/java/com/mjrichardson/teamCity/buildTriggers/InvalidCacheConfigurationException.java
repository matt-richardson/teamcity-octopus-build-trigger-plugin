package com.mjrichardson.teamCity.buildTriggers;

public class InvalidCacheConfigurationException extends Exception {
    public InvalidCacheConfigurationException(CacheManager.CacheNames cacheName) {
        super("Unable to find cache '" + cacheName.name() + "'");
    }
}
