package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_URL;

class MachineAddedAsyncBuildTrigger implements AsyncBuildTrigger<MachineAddedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;

    public MachineAddedAsyncBuildTrigger(String displayName, int pollIntervalInSeconds) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
    }

    @NotNull
    public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
    }

    @NotNull
    public String getRequestorString(@NotNull MachineAddedSpec machineAddedSpec) {
        return machineAddedSpec.getRequestorString();
    }

    public int getPollInterval(@NotNull AsyncTriggerParameters parameters) {
        return pollIntervalInSeconds;
    }

    @NotNull
    public CheckJob<MachineAddedSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new MachineAddedCheckJob(displayName,
                asyncTriggerParameters.getBuildType().toString(),
                asyncTriggerParameters.getCustomDataStorage(),
                asyncTriggerParameters.getTriggerDescriptor().getProperties());
    }

    @NotNull
    public CheckResult<MachineAddedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        return MachineAddedSpecCheckResult.createThrowableResult(throwable);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
        return String.format("Wait for a new machine to be added to server %s.",
                properties.get(OCTOPUS_URL));
    }
}
