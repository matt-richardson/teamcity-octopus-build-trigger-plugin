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

package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.codahale.metrics.MetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTrigger;
import com.mjrichardson.teamCity.buildTriggers.CustomCheckJob;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class ReleaseCreatedAsyncBuildTrigger extends CustomAsyncBuildTrigger<ReleaseCreatedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;
    private final MetricRegistry metricRegistry;

    public ReleaseCreatedAsyncBuildTrigger(String displayName, int pollIntervalInSeconds, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
    }

    @NotNull
    public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
    }

    @NotNull
    public String getRequestorString(@NotNull ReleaseCreatedSpec releaseCreatedSpec) {
        return releaseCreatedSpec.getRequestorString();
    }

    public int getPollIntervalInMilliseconds() {
        return pollIntervalInSeconds;
    }

    @NotNull
    public CustomCheckJob<ReleaseCreatedSpec> createJob(@NotNull String buildType, @NotNull CustomDataStorage dataStorage, @NotNull Map<String, String> properties, @NotNull UUID correlationId) throws CheckJobCreationException {
        return new ReleaseCreatedCheckJob(displayName,
                buildType,
                dataStorage,
                properties,
                analyticsTracker,
                cacheManager,
                metricRegistry);
    }

    @NotNull
    public CheckResult<ReleaseCreatedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable, UUID correlationId) {
        return ReleaseCreatedSpecCheckResult.createThrowableResult(throwable, correlationId);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
        return String.format("Wait for a new release of %s to be created on server %s.",
                properties.get(OCTOPUS_PROJECT_ID),
                properties.get(OCTOPUS_URL));
    }

    public Map<String, String> getProperties(ReleaseCreatedSpec releaseCreatedSpec) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(BUILD_PROPERTY_RELEASE_ID, releaseCreatedSpec.releaseId);
        hashMap.put(BUILD_PROPERTY_RELEASE_VERSION, releaseCreatedSpec.version);
        hashMap.put(BUILD_PROPERTY_RELEASE_PROJECT_ID, releaseCreatedSpec.projectId);
        return hashMap;
    }
}
