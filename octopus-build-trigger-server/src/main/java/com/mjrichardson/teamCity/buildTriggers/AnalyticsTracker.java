package com.mjrichardson.teamCity.buildTriggers;

import java.util.UUID;

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

    void postEvent(EventCategory eventCategory, EventAction eventAction, UUID correlationId);

    void postException(Exception e, UUID correlationId);

    void setOctopusVersion(String octopusVersion);

    void setOctopusApiVersion(String octopusApiVersion);
}
