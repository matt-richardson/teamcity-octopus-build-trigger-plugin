package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_URL;

class ReleaseCreatedAsyncBuildTrigger implements AsyncBuildTrigger<ReleaseCreatedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedAsyncBuildTrigger.class.getName());

    public ReleaseCreatedAsyncBuildTrigger(String displayName, int pollIntervalInSeconds) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
    }

    @NotNull
    public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
    }

    @NotNull
    public String getRequestorString(@NotNull ReleaseCreatedSpec deploymentCompleteSpec) {
        return deploymentCompleteSpec.getRequestorString();
    }

    public int getPollInterval(@NotNull AsyncTriggerParameters parameters) {
        return pollIntervalInSeconds;
    }

    @NotNull
    public CheckJob<ReleaseCreatedSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new ReleaseCreatedCheckJob(displayName,
                asyncTriggerParameters.getBuildType().toString(),
                asyncTriggerParameters.getCustomDataStorage(),
                asyncTriggerParameters.getTriggerDescriptor().getProperties());
    }

    @NotNull
    public CheckResult<ReleaseCreatedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        return ReleaseCreatedSpecCheckResult.createThrowableResult(throwable);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
        return String.format("Wait for a new release of %s to be created on server %s.",
                properties.get(OCTOPUS_PROJECT_ID),
                properties.get(OCTOPUS_URL));
    }
}
