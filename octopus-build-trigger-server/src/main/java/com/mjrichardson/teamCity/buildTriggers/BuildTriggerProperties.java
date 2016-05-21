package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.serverSide.TeamCityProperties;

public class BuildTriggerProperties {
    private static final String POLL_INTERVAL_PROP = "octopus.build.trigger.poll.interval.in.seconds";
    private static final Integer DEFAULT_POLL_INTERVAL_IN_SECONDS = 30;

    private static final String UPDATE_CHECK_ENABLED = "octopus.build.trigger.update.check.enabled";
    private static final String CACHE_ENABLED = "octopus.build.trigger.cache.enabled";
    private static final String ANALYTICS_ENABLED = "octopus.build.trigger.analytics.enabled";

    private static final String CONNECTION_TIMEOUT_PROP = "octopus.build.trigger.connection.timeout.in.milliseconds";
    private static final Integer DEFAULT_CONNECTION_TIMEOUT_IN_MILLISECONDS = 30 * 1000;

    public Integer getConnectionTimeoutInMilliseconds() {
        return TeamCityProperties.getInteger(CONNECTION_TIMEOUT_PROP, DEFAULT_CONNECTION_TIMEOUT_IN_MILLISECONDS);
    }

    public int getPollInterval() {
        return TeamCityProperties.getInteger(POLL_INTERVAL_PROP, DEFAULT_POLL_INTERVAL_IN_SECONDS);
    }

    public boolean isAnalyticsEnabled() {
        return TeamCityProperties.getBooleanOrTrue(ANALYTICS_ENABLED);
    }

    public boolean isUpdateCheckEnabled() {
        return TeamCityProperties.getBooleanOrTrue(UPDATE_CHECK_ENABLED);
    }

    public boolean isCacheEnabled() {
        return TeamCityProperties.getBooleanOrTrue(CACHE_ENABLED);
    }
}
