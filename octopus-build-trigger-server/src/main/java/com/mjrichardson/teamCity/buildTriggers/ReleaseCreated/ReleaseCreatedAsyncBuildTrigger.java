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
  private final int pollInterval;
  @NotNull
  private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTrigger.class.getName());

  public ReleaseCreatedAsyncBuildTrigger(String displayName, int pollInterval) {
    this.displayName = displayName;
    this.pollInterval = pollInterval;
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
    return pollInterval;
  }

  @NotNull
  public CheckJob<ReleaseCreatedSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
    return new ReleaseCreatedCheckJob(asyncTriggerParameters, displayName);
  }

  @NotNull
  public CheckResult<ReleaseCreatedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
    return ReleaseCreatedSpecCheckResult.createThrowableResult(throwable);
  }

  public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
    return getDescription(buildTriggerDescriptor.getProperties());
  }

  private String getDescription(Map<String, String> properties) {
    try {
      return String.format("Wait for a new release of %s to be created on server %s.",
              properties.get(OCTOPUS_PROJECT_ID),
              properties.get(OCTOPUS_URL));
    }
    catch (Exception e) {
      LOG.error("Error in describeTrigger ", e);
      return "Unable to determine trigger description";
    }
  }
}
