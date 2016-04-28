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
import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class DeploymentProcessChangedCheckJob implements CheckJob<DeploymentProcessChangedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentProcessChangedCheckJob.class.getName());

    private final String displayName;
    private final String buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;
    private final DeploymentProcessProviderFactory deploymentProcessProviderFactory;

    public DeploymentProcessChangedCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry) {
        this(new DeploymentProcessProviderFactory(analyticsTracker, cacheManager, metricRegistry), displayName, buildType, dataStorage, properties, analyticsTracker);
    }

    public DeploymentProcessChangedCheckJob(DeploymentProcessProviderFactory deploymentProcessProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this.deploymentProcessProviderFactory = deploymentProcessProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
    }

    @NotNull
    CheckResult<DeploymentProcessChangedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage) {
        LOG.debug("Checking if deployment process has changed for project " + octopusProject + " on server " + octopusUrl);
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            final String oldStoredData = dataStorage.getValue(dataStorageKey);

            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();
            DeploymentProcessProvider provider = deploymentProcessProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeoutInMilliseconds);

            final String newStoredData = provider.getDeploymentProcessVersion(octopusProject);

            //do not trigger build after first adding trigger (oldEnvironments == null)
            if (oldStoredData == null) {
                dataStorage.putValue(dataStorageKey, newStoredData);
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger, AnalyticsTracker.EventAction.TriggerAdded);

                LOG.debug("No previous data for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
                return DeploymentProcessChangedSpecCheckResult.createEmptyResult();
            }

            if (oldStoredData.equals(newStoredData)) {
                LOG.debug("oldStoredData was '" + oldStoredData + "'");
                LOG.debug("newStoredData was '" + newStoredData + "'");
                LOG.info("No changes to deployment process for project '" + octopusProject + "' on '" + octopusUrl + "'");

                return DeploymentProcessChangedSpecCheckResult.createEmptyResult();
            }

            dataStorage.putValue(dataStorageKey, newStoredData);

            analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger, AnalyticsTracker.EventAction.BuildTriggered);

            LOG.info(String.format("Deployment process for project %s on url %s: %s -> %s", octopusProject, octopusUrl, oldStoredData, newStoredData));
            final DeploymentProcessChangedSpec DeploymentProcessChangedSpec = new DeploymentProcessChangedSpec(octopusUrl, newStoredData, octopusProject);
            return DeploymentProcessChangedSpecCheckResult.createUpdatedResult(DeploymentProcessChangedSpec);
        } catch (Exception e) {
            LOG.error("Failed to check for changed deployment process", e);

            analyticsTracker.postException(e);
            return DeploymentProcessChangedSpecCheckResult.createThrowableResult(e);
        }
    }

    @NotNull
    public CheckResult<DeploymentProcessChangedSpec> perform() {

        final String octopusUrl = props.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, this.buildType));
        }

        final String octopusApiKey = props.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, this.buildType));
        }

        final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, this.buildType));
        }

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, dataStorage);
    }

    public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        //we always return false here - the AsyncPolledBuildTrigger class handles whether we are busy or not
        //also, this is inverted, the method should be preventSchedule or something
        return false;
    }
}
