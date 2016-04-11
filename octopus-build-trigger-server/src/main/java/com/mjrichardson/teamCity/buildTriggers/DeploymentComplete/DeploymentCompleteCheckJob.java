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
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class DeploymentCompleteCheckJob implements CheckJob<DeploymentCompleteSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentCompleteCheckJob.class.getName());

    private final String displayName;
    private final String buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;
    private final DeploymentsProviderFactory deploymentsProviderFactory;

    public DeploymentCompleteCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this(new DeploymentsProviderFactory(analyticsTracker), displayName, buildType, dataStorage, properties, analyticsTracker);
    }

    public DeploymentCompleteCheckJob(DeploymentsProviderFactory deploymentsProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this.deploymentsProviderFactory = deploymentsProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
    }

    @NotNull
    CheckResult<DeploymentCompleteSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject,
                                                       Boolean triggerOnlyOnSuccessfulDeployment, CustomDataStorage dataStorage) {
        LOG.debug("Checking for new deployments for project " + octopusProject + " on server " + octopusUrl);
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            final String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Environments oldEnvironments = Environments.Parse(oldStoredData);

            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();
            DeploymentsProvider provider = deploymentsProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeoutInMilliseconds);

            final Environments newEnvironments = provider.getDeployments(octopusProject, oldEnvironments);

            //only store that one deployment to one environment has happened here, not multiple environment.
            //otherwise, we could inadvertently miss deployments
            final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, triggerOnlyOnSuccessfulDeployment);

            String newStoredData = trimmedEnvironments.toString();

            //do not trigger build after first adding trigger (oldEnvironments == null)
            if (oldStoredData == null) {
                dataStorage.putValue(dataStorageKey, newEnvironments.toString());
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, AnalyticsTracker.EventAction.TriggerAdded);

                LOG.debug("No previous data for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newEnvironments);
                return DeploymentCompleteSpecCheckResult.createEmptyResult();
            }

            if (trimmedEnvironments.equals(oldEnvironments)) {
                //note: deleting an environment deletes it from the progression api response, but not the deployment api response
                if (newEnvironments.size() < oldEnvironments.size()) {
                    final Environments deletedEnvironments = trimmedEnvironments.removeEnvironmentsNotIn(newEnvironments);
                    newStoredData = trimmedEnvironments.toString();
                    dataStorage.putValue(dataStorageKey, newStoredData);

                    LOG.debug("Environments have been removed from Octopus: " + deletedEnvironments.toString());
                }
                LOG.debug("oldStoredData was '" + oldStoredData + "'");
                LOG.debug("trimmedEnvironments was '" + trimmedEnvironments + "'");
                if (triggerOnlyOnSuccessfulDeployment)
                    LOG.info("No new successful deployments on '" + octopusUrl + "' for project '" + octopusProject + "'");
                else
                    LOG.info("No new deployments on '" + octopusUrl + "' for project '" + octopusProject + "'");

                return DeploymentCompleteSpecCheckResult.createEmptyResult();
            }

            dataStorage.putValue(dataStorageKey, newStoredData);



            final Environment environment = trimmedEnvironments.getChangedDeployment(oldEnvironments);
            if (triggerOnlyOnSuccessfulDeployment && !environment.wasLatestDeploymentSuccessful()) {
                LOG.debug("New deployments found, but they weren't successful, and we are only triggering on successful builds. Server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + trimmedEnvironments);
                return DeploymentCompleteSpecCheckResult.createEmptyResult();
            }

            analyticsTracker.postEvent(AnalyticsTracker.EventCategory.DeploymentCompleteTrigger, AnalyticsTracker.EventAction.BuildTriggered);

            if (triggerOnlyOnSuccessfulDeployment && environment.wasLatestDeploymentSuccessful())
                LOG.info(String.format("New successful deployment on %s for project %s: %s -> %s", octopusUrl, octopusProject, oldStoredData, trimmedEnvironments));
            else
                LOG.info(String.format("New deployment on %s for project %s: %s -> %s", octopusUrl, octopusProject, oldStoredData, trimmedEnvironments));
            final DeploymentCompleteSpec deploymentCompleteSpec = new DeploymentCompleteSpec(octopusUrl, environment);
            return DeploymentCompleteSpecCheckResult.createUpdatedResult(deploymentCompleteSpec);
        } catch (Exception e) {
            LOG.error("Failed to check for new deployments completed", e);

            analyticsTracker.postException(e);
            return DeploymentCompleteSpecCheckResult.createThrowableResult(e);
        }
    }

    @NotNull
    public CheckResult<DeploymentCompleteSpec> perform() {

        final String octopusUrl = props.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, this.buildType));
        }

        final String octopusApiKey = props.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, this.buildType));
        }

        final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return DeploymentCompleteSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, this.buildType));
        }

        final Boolean triggerOnlyOnSuccessfulDeployment = Boolean.parseBoolean(props.get(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT));

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, triggerOnlyOnSuccessfulDeployment, dataStorage);
    }

    public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        //we always return false here - the AsyncPolledBuildTrigger class handles whether we are busy or not
        //also, this is inverted, the method should be preventSchedule or something
        return false;
    }
}
