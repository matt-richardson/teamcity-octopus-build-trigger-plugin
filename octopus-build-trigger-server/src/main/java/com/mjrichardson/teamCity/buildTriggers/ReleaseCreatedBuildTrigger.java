package com.mjrichardson.teamCity.buildTriggers;

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

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.DEFAULT_POLL_INTERVAL;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.POLL_INTERVAL_PROP;

public final class ReleaseCreatedBuildTrigger extends BuildTriggerService {
  @NotNull
  private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTrigger.class.getName());
  @NotNull
  private final PluginDescriptor myPluginDescriptor;
  @NotNull
  private final BuildTriggeringPolicy myPolicy;

  public ReleaseCreatedBuildTrigger(@NotNull final PluginDescriptor pluginDescriptor,
                                        @NotNull final AsyncBuildTriggerFactory triggerFactory) {
    myPluginDescriptor = pluginDescriptor;
    myPolicy = triggerFactory.createBuildTrigger(ReleaseCreatedSpec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
  }

  @NotNull
  @Override
  public String getName() {
    return "octopusReleaseCreatedTrigger";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Octopus Release Created Trigger";
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
    return new ReleaseCreatedTriggerPropertiesProcessor();
  }

  @Override
  public String getEditParametersUrl() {
    return myPluginDescriptor.getPluginResourcesPath("editOctopusReleaseCreatedTrigger.jsp");
  }

  @Override
  public boolean isMultipleTriggersPerBuildTypeAllowed() {
    return true;
  }

  @NotNull
  private AsyncBuildTrigger<ReleaseCreatedSpec> getAsyncBuildTrigger() {
    return getBuildTrigger();
  }

  @NotNull
  private int getPollInterval() {
    return TeamCityProperties.getInteger(POLL_INTERVAL_PROP, DEFAULT_POLL_INTERVAL);
  }

  @NotNull
  private ReleaseCreatedAsyncBuildTrigger getBuildTrigger() {
    return new ReleaseCreatedAsyncBuildTrigger(getDisplayName(), getPollInterval());
  }
}
