package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTriggerFactory;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public final class ReleaseCreatedBuildTriggerService extends BuildTriggerService {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTriggerService.class.getName());
    @NotNull
    private final PluginDescriptor myPluginDescriptor;
    @NotNull
    private final AnalyticsTracker analyticsTracker;
    @NotNull
    private final BuildTriggeringPolicy myPolicy;

    public ReleaseCreatedBuildTriggerService(@NotNull final PluginDescriptor pluginDescriptor,
                                             @NotNull final AsyncBuildTriggerFactory triggerFactory,
                                             @NotNull final AnalyticsTracker analyticsTracker) {
        myPluginDescriptor = pluginDescriptor;
        this.analyticsTracker = analyticsTracker;
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
        return OctopusBuildTriggerUtil.getPollInterval();
    }

    @NotNull
    private ReleaseCreatedAsyncBuildTrigger getBuildTrigger() {
        return new ReleaseCreatedAsyncBuildTrigger(getDisplayName(), getPollInterval(), analyticsTracker);
    }
}
