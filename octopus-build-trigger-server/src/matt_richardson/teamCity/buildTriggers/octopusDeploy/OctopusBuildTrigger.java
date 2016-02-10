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

//todo: move back under the jetbrains namespace? (https://confluence.jetbrains.com/display/TCD9/Plugin+Development+FAQ)
package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTriggerFactory;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.DEFAULT_POLL_INTERVAL;
import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.POLL_INTERVAL_PROP;

public final class OctopusBuildTrigger extends BuildTriggerService {
  @NotNull
  private static final Logger LOG = Logger.getInstance(OctopusBuildTrigger.class.getName());
  @NotNull
  private final PluginDescriptor myPluginDescriptor;
  @NotNull
  private final BuildTriggeringPolicy myPolicy;

  public OctopusBuildTrigger(@NotNull final PluginDescriptor pluginDescriptor,
                             @NotNull final AsyncBuildTriggerFactory triggerFactory) {
    myPluginDescriptor = pluginDescriptor;
    myPolicy = triggerFactory.createBuildTrigger(Spec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
  }

  @NotNull
  @Override
  public String getName() {
    return "octopusBuildTrigger";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Octopus Build Trigger";
  }

  @NotNull
  @Override
  public String describeTrigger(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
    return getBuildTrigger().describeTrigger(buildTriggerDescriptor);
  }

  @NotNull
  @Override
  public BuildTriggeringPolicy getBuildTriggeringPolicy() {
    return myPolicy;
  }

  @Override
  public PropertiesProcessor getTriggerPropertiesProcessor() {
    return new OctopusBuildTriggerPropertiesProcessor();
  }

  @Override
  public String getEditParametersUrl() {
    return myPluginDescriptor.getPluginResourcesPath("editOctopusBuildTrigger.jsp");
  }

  @Override
  public boolean isMultipleTriggersPerBuildTypeAllowed() {
    return true;
  }

  @NotNull
  private AsyncBuildTrigger<Spec> getAsyncBuildTrigger() {
    return getBuildTrigger();
  }

  @NotNull
  private int getPollInterval() {
    return TeamCityProperties.getInteger(POLL_INTERVAL_PROP, DEFAULT_POLL_INTERVAL);
  }

  @NotNull
  private DeploymentCompleteAsyncBuildTrigger getBuildTrigger() {
    return new DeploymentCompleteAsyncBuildTrigger(getDisplayName(), getPollInterval());
  }
}
