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

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.OCTOPUS_APIKEY;
import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.OCTOPUS_URL;

class MachineAddedCheckJob extends CustomCheckJob<MachineAddedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(MachineAddedCheckJob.class.getName());

    private final MachinesProviderFactory MachinesProviderFactory;
    private final String displayName;
    private final SBuildType buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;
    private final BuildTriggerProperties buildTriggerProperties;

    public MachineAddedCheckJob(String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties) {
        this(new MachinesProviderFactory(analyticsTracker, cacheManager, metricRegistry), displayName, buildType, dataStorage, properties, analyticsTracker, buildTriggerProperties);
    }

    public MachineAddedCheckJob(MachinesProviderFactory MachinesProviderFactory, String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, BuildTriggerProperties buildTriggerProperties) {
        this.MachinesProviderFactory = MachinesProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
        this.buildTriggerProperties = buildTriggerProperties;
    }

    @NotNull
    CheckResult<MachineAddedSpec> getCheckResult(String octopusUrl, String octopusApiKey, CustomDataStorage dataStorage, UUID correlationId) {
        LOG.debug(String.format("%s: Checking for new machines for on server %s", correlationId, octopusUrl));
        final String dataStorageKey = (displayName + "|" + octopusUrl).toLowerCase();

        try {
            String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Machines oldMachines = Machines.Parse(oldStoredData);

            MachinesProvider provider = MachinesProviderFactory.getProvider(octopusUrl, octopusApiKey, buildTriggerProperties);
            final Machines newMachines = provider.getMachines(correlationId);

            //only store that one machine was added here, not multiple.
            //otherwise, we could inadvertently miss new machines
            final Machine newMachine = newMachines.getNextMachine(oldMachines);
            final Machines trimmedMachines = Machines.Parse(oldStoredData);
            trimmedMachines.add(newMachine);
            String newStoredData = trimmedMachines.toString();

            if (newStoredData.equals(oldStoredData)) {
                if (newMachines.size() < oldMachines.size()) {
                    final Machines deletedMachines = trimmedMachines.removeMachinesNotIn(newMachines);
                    newStoredData = trimmedMachines.toString();
                    dataStorage.putValue(dataStorageKey, newStoredData);

                    LOG.debug(String.format("%s: Machines have been removed from Octopus: %s", correlationId, deletedMachines.toString()));
                }

                LOG.debug(String.format("%s: oldStoredData was '%s'", correlationId, oldStoredData));
                LOG.debug(String.format("%s: newStoredData was '%s'", correlationId, newStoredData));
                LOG.info(String.format("%s: No new machines on '%s'", correlationId, octopusUrl));
                return MachineAddedSpecCheckResult.createEmptyResult(correlationId);
            }

            //do not trigger build after first adding trigger (oldMachines == null)
            if (oldStoredData == null) {
                //store all existing machines, so we only trigger on new ones added after this point
                dataStorage.putValue(dataStorageKey, newMachines.toString());
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.MachineAddedTrigger, AnalyticsTracker.EventAction.TriggerAdded, correlationId);

                LOG.debug(String.format("%s: No previously known machines known for server %s: null -> %s", correlationId, octopusUrl, newStoredData));
                return MachineAddedSpecCheckResult.createEmptyResult(correlationId);
            }
            dataStorage.putValue(dataStorageKey, newStoredData);

            analyticsTracker.postEvent(AnalyticsTracker.EventCategory.MachineAddedTrigger, AnalyticsTracker.EventAction.BuildTriggered, correlationId);

            LOG.info(String.format("%s: New Machine %s created on %s: %s -> %s", correlationId, newMachine.name, octopusUrl, oldStoredData, newStoredData));
            final MachineAddedSpec MachineAddedSpec = new MachineAddedSpec(octopusUrl, newMachine);
            return MachineAddedSpecCheckResult.createUpdatedResult(MachineAddedSpec, correlationId);
        } catch (Exception e) {
            LOG.error(String.format("%s: Failed to check for new machines added", correlationId), e);

            analyticsTracker.postException(e, correlationId);

            return MachineAddedSpecCheckResult.createThrowableResult(e, correlationId);
        }
    }

    @NotNull
    public CheckResult<MachineAddedSpec> perform(UUID correlationId) {
        ValueResolver resolver = this.buildType.getValueResolver();
        final String octopusUrl = resolveValue(resolver, props.get(OCTOPUS_URL));
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return MachineAddedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, buildType), correlationId);
        }

        final String octopusApiKey = resolveValue(resolver, props.get(OCTOPUS_APIKEY));
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return MachineAddedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, buildType), correlationId);
        }

        return getCheckResult(octopusUrl, octopusApiKey, dataStorage, correlationId);
    }
}
