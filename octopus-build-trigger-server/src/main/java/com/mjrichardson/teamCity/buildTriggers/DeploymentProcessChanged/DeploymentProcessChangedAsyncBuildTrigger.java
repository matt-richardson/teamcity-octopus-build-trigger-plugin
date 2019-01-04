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

package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.codahale.metrics.MetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.*;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.*;

public class DeploymentProcessChangedAsyncBuildTrigger extends CustomAsyncBuildTrigger<DeploymentProcessChangedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    private final AnalyticsTracker analyticsTracker;
    private CacheManager cacheManager;
    private MetricRegistry metricRegistry;
    private final BuildTriggerProperties buildTriggerProperties;

    public DeploymentProcessChangedAsyncBuildTrigger(String displayName, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = buildTriggerProperties.getPollInterval();
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
        this.buildTriggerProperties = buildTriggerProperties;
    }

    @NotNull
    public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
    }

    @NotNull
    public String getRequestorString(@NotNull DeploymentProcessChangedSpec DeploymentProcessChangedSpec) {
        return DeploymentProcessChangedSpec.getRequestorString();
    }

    public int getPollIntervalInMilliseconds() {
        return pollIntervalInSeconds;
    }

    @NotNull
    public CustomCheckJob<DeploymentProcessChangedSpec> createJob(@NotNull SBuildType buildType, @NotNull CustomDataStorage dataStorage, @NotNull Map<String, String> properties, @NotNull UUID correlationId) throws CheckJobCreationException {
        return new DeploymentProcessChangedCheckJob(displayName,
                buildType,
                dataStorage,
                properties,
                analyticsTracker,
                cacheManager,
                metricRegistry, buildTriggerProperties);
    }

    @NotNull
    public CheckResult<DeploymentProcessChangedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable, UUID correlationId) {
        return DeploymentProcessChangedSpecCheckResult.createThrowableResult(throwable, correlationId);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
        return String.format("Wait for a change in the deployment process of %s on server %s.",
                properties.get(OCTOPUS_PROJECT_ID),
                properties.get(OCTOPUS_URL));
    }

    public Map<String, String> getProperties(DeploymentProcessChangedSpec DeploymentProcessChangedSpec) {
        //todo: add deployment name
        //todo: add environment name - needs to remove the fallback though
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_PROCESS_VERSION, DeploymentProcessChangedSpec.version);
        hashMap.put(BUILD_PROPERTY_DEPLOYMENT_PROCESS_PROJECT_ID, DeploymentProcessChangedSpec.projectId);
        return hashMap;
    }
}
