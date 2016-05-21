package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.BuildTriggerProperties;

public class FakeBuildTriggerProperties extends BuildTriggerProperties {
    @Override
    public boolean isAnalyticsEnabled() {
        return true;
    }

    @Override
    public boolean isCacheEnabled() {
        return true;
    }

    @Override
    public boolean isUpdateCheckEnabled() {
        return true;
    }

    @Override
    public int getPollInterval() {
        return 30;
    }

    @Override
    public Integer getConnectionTimeoutInMilliseconds() {
        return 30 * 1000;
    }
}
