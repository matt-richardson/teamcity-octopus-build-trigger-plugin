package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;

public class FakeAnalyticsTracker implements AnalyticsTracker {
    @Override
    public void postEvent(EventCategory eventCategory, EventAction eventAction) {
        //no-op
    }

    @Override
    public void postException(Exception e) {
        //no-op
    }
}
