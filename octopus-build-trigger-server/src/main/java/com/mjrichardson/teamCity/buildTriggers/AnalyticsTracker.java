package com.mjrichardson.teamCity.buildTriggers;

public interface AnalyticsTracker {
    enum EventCategory {
        MachineAddedTrigger,
        ReleaseCreatedTrigger,
        DeploymentCompleteTrigger
    }

    enum EventAction {
        BuildTriggered,
        TriggerAdded,
        FallingBackToDeploymentsApi,
        FallBackToDeploymentsApiProducedBetterInformation,
        FallBackToDeploymentsApiProducedSameResults,
        FallBackToDeploymentsApiProducedWorseResults,
        FallBackToDeploymentsApiProducedDifferentNumberOfEnvironments,
        FallBackToDeploymentsApiProducedDifferentEnvironments
    }

    void postEvent(EventCategory eventCategory, EventAction eventAction);

    void postException(Exception e);
}
