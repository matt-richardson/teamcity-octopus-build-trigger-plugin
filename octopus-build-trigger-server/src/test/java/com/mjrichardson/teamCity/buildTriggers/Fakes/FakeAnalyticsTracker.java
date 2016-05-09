package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;

import java.util.UUID;

public class FakeAnalyticsTracker implements AnalyticsTracker {
    public int receivedPostCount;
    public EventAction eventAction;
    public EventCategory eventCategory;
    public String octopusVersion;
    public String octopusApiVersion;

    @Override
    public void postEvent(EventCategory eventCategory, EventAction eventAction, UUID correlationId) {
        this.receivedPostCount++;
        this.eventCategory = eventCategory;
        this.eventAction = eventAction;
    }

    @Override
    public void postException(Exception e, UUID correlationId) {
        //no-op
    }

    @Override
    public void setOctopusVersion(String octopusVersion) {
        this.octopusVersion = octopusVersion;
    }

    @Override
    public void setOctopusApiVersion(String octopusApiVersion) {
        this.octopusApiVersion = octopusApiVersion;
    }
}
