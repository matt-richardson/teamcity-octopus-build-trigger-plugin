package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTriggerFactory;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public final class MachineAddedBuildTriggerService extends BuildTriggerService {
    @NotNull
    private static final Logger LOG = Logger.getInstance(MachineAddedBuildTriggerService.class.getName());
    @NotNull
    private final PluginDescriptor myPluginDescriptor;
    @NotNull
    private final BuildTriggeringPolicy myPolicy;

    public MachineAddedBuildTriggerService(@NotNull final PluginDescriptor pluginDescriptor,
                                             @NotNull final AsyncBuildTriggerFactory triggerFactory) {
        myPluginDescriptor = pluginDescriptor;
        myPolicy = triggerFactory.createBuildTrigger(MachineAddedSpec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
    }

    @NotNull
    @Override
    public String getName() {
        return "octopusMachineAddedTrigger";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Octopus Machine Added Trigger";
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
        return new MachineAddedTriggerPropertiesProcessor();
    }

    @Override
    public String getEditParametersUrl() {
        return myPluginDescriptor.getPluginResourcesPath("editOctopusMachineAddedTrigger.jsp");
    }

    @Override
    public boolean isMultipleTriggersPerBuildTypeAllowed() {
        return true;
    }

    @NotNull
    private AsyncBuildTrigger<MachineAddedSpec> getAsyncBuildTrigger() {
        return getBuildTrigger();
    }

    @NotNull
    private int getPollInterval() {
        return OctopusBuildTriggerUtil.getPollInterval();
    }

    @NotNull
    private MachineAddedAsyncBuildTrigger getBuildTrigger() {
        return new MachineAddedAsyncBuildTrigger(getDisplayName(), getPollInterval());
    }
}
