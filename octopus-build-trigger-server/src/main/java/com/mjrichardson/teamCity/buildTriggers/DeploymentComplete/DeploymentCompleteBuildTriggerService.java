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
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTrigger;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTriggerFactory;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public final class DeploymentCompleteBuildTriggerService extends BuildTriggerService {
    @NotNull
    private static final Logger LOG = Logger.getInstance(DeploymentCompleteBuildTriggerService.class.getName());
    @NotNull
    private final PluginDescriptor myPluginDescriptor;
    @NotNull
    private final AnalyticsTracker analyticsTracker;
    @NotNull
    private final BuildTriggeringPolicy myPolicy;

    public DeploymentCompleteBuildTriggerService(@NotNull final PluginDescriptor pluginDescriptor,
                                                 @NotNull final CustomAsyncBuildTriggerFactory triggerFactory,
                                                 @NotNull final AnalyticsTracker analyticsTracker) {
        myPluginDescriptor = pluginDescriptor;
        this.analyticsTracker = analyticsTracker;
        myPolicy = triggerFactory.createBuildTrigger(DeploymentCompleteSpec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
    }

    @NotNull
    @Override
    public String getName() {
        return "octopusDeploymentCompleteTrigger";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Octopus Deployment Complete Trigger";
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
        return new DeploymentCompleteTriggerPropertiesProcessor();
    }

    @Override
    public String getEditParametersUrl() {
        return myPluginDescriptor.getPluginResourcesPath("editOctopusDeploymentCompleteTrigger.jsp");
    }

    @Override
    public boolean isMultipleTriggersPerBuildTypeAllowed() {
        return true;
    }

    @NotNull
    private CustomAsyncBuildTrigger<DeploymentCompleteSpec> getAsyncBuildTrigger() {
        return getBuildTrigger();
    }

    @NotNull
    private int getPollInterval() {
        return OctopusBuildTriggerUtil.getPollInterval();
    }

    @NotNull
    private DeploymentCompleteAsyncBuildTrigger getBuildTrigger() {
        return new DeploymentCompleteAsyncBuildTrigger(getDisplayName(), getPollInterval(), analyticsTracker);
    }
}
