package com.mjrichardson.teamCity.buildTriggers;

public interface AnalyticsTracker {
    enum EventCategory {
        MachineAddedTrigger,
        ReleaseCreatedTrigger,
        DeploymentCompleteTrigger,
        DeploymentProcessChangedTrigger
    }

    enum EventAction {
        BuildTriggered,
        TriggerAdded,
        FallingBackToDeploymentsApi,
        FallBackToDeploymentsApiProducedBetterInformation,
        FallBackToDeploymentsApiProducedSameResults,
        FallBackToDeploymentsApiProducedWorseResults,
        FallBackToDeploymentsApiProducedFewerEnvironments,
        FallBackToDeploymentsApiProducedMoreEnvironments,
        FallBackToDeploymentsApiProducedDifferentEnvironments,
        FallBackStatusUnknown
    }

    void postEvent(EventCategory eventCategory, EventAction eventAction);

    void postException(Exception e);

    void setOctopusVersion(String octopusVersion);

    void setOctopusApiVersion(String octopusApiVersion);
}
