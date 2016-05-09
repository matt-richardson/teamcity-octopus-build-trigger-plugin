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
import com.mjrichardson.teamCity.buildTriggers.CustomCheckJob;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class DeploymentProcessChangedCheckJob extends CustomCheckJob<DeploymentProcessChangedSpec> {
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
    CheckResult<DeploymentProcessChangedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage, UUID correlationId) {
        LOG.debug(String.format("%s: Checking if deployment process has changed for project %s on server %s", correlationId, octopusProject, octopusUrl));
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            final String oldStoredData = dataStorage.getValue(dataStorageKey);

            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();
            DeploymentProcessProvider provider = deploymentProcessProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeoutInMilliseconds);

            final String newStoredData = provider.getDeploymentProcessVersion(octopusProject, correlationId);

            //do not trigger build after first adding trigger (oldEnvironments == null)
            if (oldStoredData == null) {
                dataStorage.putValue(dataStorageKey, newStoredData);
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger, AnalyticsTracker.EventAction.TriggerAdded, correlationId);

                LOG.debug(String.format("%s: No previous data for server %s, project %s: null -> %s", correlationId, octopusUrl, octopusProject, newStoredData));
                return DeploymentProcessChangedSpecCheckResult.createEmptyResult(correlationId);
            }

            if (oldStoredData.equals(newStoredData)) {
                LOG.debug(String.format("%s: oldStoredData was '%s'", correlationId, oldStoredData));
                LOG.debug(String.format("%s: newStoredData was '%s'", correlationId, newStoredData));
                LOG.info(String.format("%s: No changes to deployment process for project '%s' on '%s'", correlationId, octopusProject, octopusUrl));

                return DeploymentProcessChangedSpecCheckResult.createEmptyResult(correlationId);
            }

            dataStorage.putValue(dataStorageKey, newStoredData);

            analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentProcessChangedTrigger, AnalyticsTracker.EventAction.BuildTriggered, correlationId);

            LOG.info(String.format("%s: Deployment process for project %s on url %s: %s -> %s", correlationId, octopusProject, octopusUrl, oldStoredData, newStoredData));
            final DeploymentProcessChangedSpec DeploymentProcessChangedSpec = new DeploymentProcessChangedSpec(octopusUrl, newStoredData, octopusProject);
            return DeploymentProcessChangedSpecCheckResult.createUpdatedResult(DeploymentProcessChangedSpec, correlationId);
        } catch (Exception e) {
            LOG.error(String.format("%s: Failed to check for changed deployment process", correlationId), e);

            analyticsTracker.postException(e, correlationId);
            return DeploymentProcessChangedSpecCheckResult.createThrowableResult(e, correlationId);
        }
    }

    @NotNull
    public CheckResult<DeploymentProcessChangedSpec> perform(UUID correlationId) {
        final String octopusUrl = props.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        final String octopusApiKey = props.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return DeploymentProcessChangedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, dataStorage, correlationId);
    }
}
