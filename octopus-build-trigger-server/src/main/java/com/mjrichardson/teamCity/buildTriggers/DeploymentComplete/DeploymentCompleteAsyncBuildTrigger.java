/*
 * Copyright 2016 Matt Richardson.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Based on code graciously open sourced by JetBrains s.r.o
 * (http://svn.jetbrains.org/teamcity/plugins/url-build-trigger/trunk/url-build-trigger-server/src/jetbrains/buildServer/buildTriggers/url/UrlBuildTrigger.java)
 *
 * Original licence:
 *
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

package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.AsyncTriggerParameters;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class DeploymentCompleteAsyncBuildTrigger implements CustomAsyncBuildTrigger<DeploymentCompleteSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    private final AnalyticsTracker analyticsTracker;
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentCompleteAsyncBuildTrigger.class.getName());

    public DeploymentCompleteAsyncBuildTrigger(String displayName, int pollIntervalInSeconds, AnalyticsTracker analyticsTracker) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
        this.analyticsTracker = analyticsTracker;
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
        return pollIntervalInSeconds;
    }

    @NotNull
    public CheckJob<DeploymentCompleteSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new DeploymentCompleteCheckJob(displayName,
                asyncTriggerParameters.getBuildType().toString(),
                asyncTriggerParameters.getCustomDataStorage(),
                asyncTriggerParameters.getTriggerDescriptor().getProperties(),
                analyticsTracker);
    }

    @NotNull
    public CheckResult<DeploymentCompleteSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        return DeploymentCompleteSpecCheckResult.createThrowableResult(throwable);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
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

    @Override
    public Map<String, String> getProperties(DeploymentCompleteSpec deploymentCompleteSpec) {
        //todo: add deployment name
        //todo: add environment name - needs to remove the fallback though
        HashMap hashMap = new HashMap();
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_ID, deploymentCompleteSpec.deploymentId);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_VERSION, deploymentCompleteSpec.version);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_PROJECT_ID, deploymentCompleteSpec.projectId);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_RELEASE_ID, deploymentCompleteSpec.releaseId);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_ENVIRONMENT_ID, deploymentCompleteSpec.environmentId);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_SUCCESSFUL, deploymentCompleteSpec.wasSuccessful.toString());
        return hashMap;
    }
}
