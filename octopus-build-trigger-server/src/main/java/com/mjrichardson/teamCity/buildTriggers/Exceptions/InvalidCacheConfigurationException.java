package com.mjrichardson.teamCity.buildTriggers.Exceptions;

import com.mjrichardson.teamCity.buildTriggers.CacheManager;

public class InvalidCacheConfigurationException extends Exception {
    public InvalidCacheConfigurationException(CacheManager.CacheNames cacheName) {
        super("Unable to find cache '" + cacheName.name() + "'");
    }
}
