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

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.BuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.CustomCheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.*;

class DeploymentCompleteCheckJob extends CustomCheckJob<DeploymentCompleteSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentCompleteCheckJob.class.getName());

    private final String displayName;
    private final SBuildType buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;
    private final DeploymentsProviderFactory deploymentsProviderFactory;
    private final BuildTriggerProperties buildTriggerProperties;

    public DeploymentCompleteCheckJob(String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties) {
        this(new DeploymentsProviderFactory(analyticsTracker, cacheManager, metricRegistry), displayName, buildType, dataStorage, properties, analyticsTracker, buildTriggerProperties);
    }

    public DeploymentCompleteCheckJob(DeploymentsProviderFactory deploymentsProviderFactory, String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, BuildTriggerProperties buildTriggerProperties) {
        this.deploymentsProviderFactory = deploymentsProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
        this.buildTriggerProperties = buildTriggerProperties;
    }

    @NotNull
    CheckResult<DeploymentCompleteSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject,
                                                       Boolean triggerOnlyOnSuccessfulDeployment, CustomDataStorage dataStorage, UUID correlationId) {
        LOG.debug(String.format("%s: Checking for new deployments for project %s on server %s", correlationId, octopusProject, octopusUrl));
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            final String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Environments oldEnvironments = Environments.Parse(oldStoredData);

            DeploymentsProvider provider = deploymentsProviderFactory.getProvider(octopusUrl, octopusApiKey, buildTriggerProperties);

            final Environments newEnvironments = provider.getDeployments(octopusProject, oldEnvironments, correlationId);

            //only store that one deployment to one environment has happened here, not multiple environment.
            //otherwise, we could inadvertently miss deployments
            final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, triggerOnlyOnSuccessfulDeployment);

            String newStoredData = trimmedEnvironments.toString();

            //do not trigger build after first adding trigger (oldEnvironments == null)
            if (oldStoredData == null) {
                dataStorage.putValue(dataStorageKey, newEnvironments.toString());
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, AnalyticsTracker.EventAction.TriggerAdded, correlationId);

                LOG.debug(String.format("%s: No previous data for server %s, project %s: null -> %s", correlationId, octopusUrl, octopusProject, newEnvironments));
                return DeploymentCompleteSpecCheckResult.createEmptyResult(correlationId);
            }

            if (trimmedEnvironments.equals(oldEnvironments)) {
                //note: deleting an environment deletes it from the progression api response, but not the deployment api response
                if (newEnvironments.size() < oldEnvironments.size()) {
                    final Environments deletedEnvironments = trimmedEnvironments.removeEnvironmentsNotIn(newEnvironments);
                    newStoredData = trimmedEnvironments.toString();
                    dataStorage.putValue(dataStorageKey, newStoredData);

                    LOG.debug(String.format("%s: Environments have been removed from Octopus: %s", correlationId, deletedEnvironments.toString()));
                }
                LOG.debug(String.format("%s: oldStoredData was '%s'", correlationId, oldStoredData));
                LOG.debug(String.format("%s: trimmedEnvironments was '%s'", correlationId, trimmedEnvironments));
                if (triggerOnlyOnSuccessfulDeployment)
                    LOG.info(String.format("%s: No new successful deployments on '%s' for project '%s'", correlationId, octopusUrl, octopusProject));
                else
                    LOG.info(String.format("%s: No new deployments on '%s' for project '%s'", correlationId, octopusUrl, octopusProject));

                return DeploymentCompleteSpecCheckResult.createEmptyResult(correlationId);
            }

            dataStorage.putValue(dataStorageKey, newStoredData);



            final Environment environment = trimmedEnvironments.getChangedDeployment(oldEnvironments);
            if (triggerOnlyOnSuccessfulDeployment && !environment.wasLatestDeploymentSuccessful()) {
                LOG.debug(String.format("%s: New deployments found, but they weren't successful, and we are only triggering on successful builds. Server %s, project %s: null -> %s", correlationId, octopusUrl, octopusProject, trimmedEnvironments));
                return DeploymentCompleteSpecCheckResult.createEmptyResult(correlationId);
            }

            analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, AnalyticsTracker.EventAction.BuildTriggered, correlationId);

            if (triggerOnlyOnSuccessfulDeployment && environment.wasLatestDeploymentSuccessful())
                LOG.info(String.format("%s: New successful deployment on %s for project %s: %s -> %s", correlationId, octopusUrl, octopusProject, oldStoredData, trimmedEnvironments));
            else
                LOG.info(String.format("%s: New deployment on %s for project %s: %s -> %s", correlationId, octopusUrl, octopusProject, oldStoredData, trimmedEnvironments));
            final DeploymentCompleteSpec deploymentCompleteSpec = new DeploymentCompleteSpec(octopusUrl, environment);
            return DeploymentCompleteSpecCheckResult.createUpdatedResult(deploymentCompleteSpec, correlationId);
        } catch (Exception e) {
            LOG.error(String.format("%s: Failed to check for new deployments completed", correlationId), e);

            analyticsTracker.postException(e, correlationId);
            return DeploymentCompleteSpecCheckResult.createThrowableResult(e, correlationId);
        }
    }

    @NotNull
    public CheckResult<DeploymentCompleteSpec> perform(UUID correlationId) {
        ValueResolver resolver = this.buildType.getValueResolver();
        final String octopusUrl = resolveValue(resolver, props.get(OCTOPUS_URL));
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        final String octopusApiKey = resolveValue(resolver, props.get(OCTOPUS_APIKEY));
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        final String octopusProject = resolveValue(resolver, props.get(OCTOPUS_PROJECT_ID));
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, this.buildType), correlationId);
        }

        final Boolean triggerOnlyOnSuccessfulDeployment = Boolean.parseBoolean(props.get(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT));

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, triggerOnlyOnSuccessfulDeployment, dataStorage, correlationId);
    }
}
