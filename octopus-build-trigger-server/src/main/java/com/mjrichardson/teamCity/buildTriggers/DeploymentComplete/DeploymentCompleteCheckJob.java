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

package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.intellij.openapi.diagnostic.Logger;
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
  private final DeploymentsProviderFactory deploymentsProviderFactory;

  public DeploymentCompleteCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties) {
    this(new DeploymentsProviderFactory(), displayName, buildType, dataStorage, properties);
  }

  public DeploymentCompleteCheckJob(DeploymentsProviderFactory deploymentsProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties) {
    this.deploymentsProviderFactory = deploymentsProviderFactory;
    this.displayName = displayName;
    this.buildType = buildType;
    this.dataStorage = dataStorage;
    this.props = properties;
  }

  @NotNull
  CheckResult<DeploymentCompleteSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject,
                                                     Boolean triggerOnlyOnSuccessfulDeployment, CustomDataStorage dataStorage) {
    LOG.debug("Checking for new deployments for project " + octopusProject + " on server " + octopusUrl);
    final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

    try {
      final String oldStoredData = dataStorage.getValue(dataStorageKey);
      final Deployments oldDeployments = new Deployments(oldStoredData);

      final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix
      DeploymentsProvider provider = deploymentsProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeout);

      final Deployments newDeployments = provider.getDeployments(octopusProject, oldDeployments);

      //only store that one deployment to one environment has happened here, not multiple environment.
      //otherwise, we could inadvertently miss deployments
      final Deployments newStoredData = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, triggerOnlyOnSuccessfulDeployment);

      if (!newDeployments.toString().equals(oldDeployments.toString())) {
        dataStorage.putValue(dataStorageKey, newStoredData.toString());

        //todo: see if its possible to to check the property on the context that says whether its new?
        //http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/buildTriggers/PolledTriggerContext.html#getPreviousCallTime()
        //do not trigger build after first adding trigger (oldDeployments == null)
        if (oldDeployments.isEmpty()) {
          LOG.debug("No previous data for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
          return DeploymentCompleteSpecCheckResult.createEmptyResult();
        }

        final Deployment deployment = newStoredData.getChangedDeployment(oldDeployments);
        if (triggerOnlyOnSuccessfulDeployment && !deployment.isSuccessful()) {
          LOG.debug("New deployments found, but they weren't successful, and we are only triggering on successful builds. Server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
          return DeploymentCompleteSpecCheckResult.createEmptyResult();
        }

        LOG.info("New deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
        final DeploymentCompleteSpec deploymentCompleteSpec = new DeploymentCompleteSpec(octopusUrl, octopusProject, deployment.environmentId, deployment.isSuccessful());
        //todo: investigate passing multiple bits to createUpdatedResult()
        return DeploymentCompleteSpecCheckResult.createUpdatedResult(deploymentCompleteSpec);
      }

      LOG.info("No new deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
      return DeploymentCompleteSpecCheckResult.createEmptyResult();

    } catch (Exception e) {
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
    return false;
  }
}
