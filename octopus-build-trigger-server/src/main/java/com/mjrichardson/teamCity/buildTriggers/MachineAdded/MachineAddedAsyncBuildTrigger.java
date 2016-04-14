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

package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
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

class MachineAddedAsyncBuildTrigger implements CustomAsyncBuildTrigger<MachineAddedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    private final AnalyticsTracker analyticsTracker;
    private final CacheManager cacheManager;

    public MachineAddedAsyncBuildTrigger(String displayName, int pollIntervalInSeconds, AnalyticsTracker analyticsTracker, CacheManager cacheManager) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
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
                asyncTriggerParameters.getTriggerDescriptor().getProperties(),
                analyticsTracker,
                cacheManager);
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

    @Override
    public Map<String, String> getProperties(MachineAddedSpec machineAddedSpec) {
        HashMap hashMap = new HashMap();
        //todo: unchecked error here
        hashMap.put(BUILD_PROPERTY_MACHINE_NAME, machineAddedSpec.machineName);
        hashMap.put(BUILD_PROPERTY_MACHINE_ID, machineAddedSpec.machineId);
        hashMap.put(BUILD_PROPERTY_MACHINE_ENVIRONMENT_IDS, machineAddedSpec.environmentIds);
        hashMap.put(BUILD_PROPERTY_MACHINE_ROLE_IDS, machineAddedSpec.roleIds);
        return hashMap;
    }
}
