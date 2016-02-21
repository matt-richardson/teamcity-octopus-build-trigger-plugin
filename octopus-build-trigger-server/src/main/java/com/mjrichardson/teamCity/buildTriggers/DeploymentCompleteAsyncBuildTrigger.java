/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_URL;

class DeploymentCompleteAsyncBuildTrigger implements AsyncBuildTrigger<DeploymentCompleteSpec> {
  private final String displayName;
  private final int pollInterval;
  @NotNull
  private static final Logger LOG = Logger.getInstance(DeploymentCompleteBuildTrigger.class.getName());

  public DeploymentCompleteAsyncBuildTrigger(String displayName, int pollInterval) {
    this.displayName = displayName;
    this.pollInterval = pollInterval;
  }

  @NotNull
  public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
    throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
  }

  @NotNull
  public String getRequestorString(@NotNull DeploymentCompleteSpec deploymentCompleteSpec) {
    return deploymentCompleteSpec.getRequestorString();
  }

  public int getPollInterval(@NotNull AsyncTriggerParameters parameters) {
    return pollInterval;
  }

  @NotNull
  public CheckJob<DeploymentCompleteSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
    return new DeploymentCompleteCheckJob(asyncTriggerParameters, displayName);
  }

  @NotNull
  public CheckResult<DeploymentCompleteSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
    return DeploymentCompleteSpecCheckResult.createThrowableResult(throwable);
  }

  public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
    return getDescription(buildTriggerDescriptor.getProperties());
  }

  private String getDescription(Map<String, String> properties) {
    try {
      String flag = properties.get(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT);
      if (flag != null && flag.equals("true")) {
        return String.format("Wait for a new successful deployment of %s on server %s.",
          properties.get(OCTOPUS_PROJECT_ID),
          properties.get(OCTOPUS_URL));
      }
      return String.format("Wait for a new deployment of %s on server %s.",
        properties.get(OCTOPUS_PROJECT_ID),
        properties.get(OCTOPUS_URL));
    }
    catch (Exception e) {
      LOG.error("Error in describeTrigger ", e);
      return "Unable to determine trigger description";
    }
  }
}
