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

package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public final class ReleaseCreatedBuildTriggerService extends BuildTriggerService {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTriggerService.class.getName());
    @NotNull
    private final PluginDescriptor pluginDescriptor;
    @NotNull
    private final AnalyticsTracker analyticsTracker;
    @NotNull
    private final CacheManager cacheManager;
    @NotNull
    private final BuildTriggeringPolicy buildTriggeringPolicy;
    @NotNull
    private final MetricRegistry metricRegistry;
    @NotNull
    private final BuildTriggerProperties buildTriggerProperties;

    public ReleaseCreatedBuildTriggerService(@NotNull final PluginDescriptor pluginDescriptor,
                                             @NotNull final CustomAsyncBuildTriggerFactory triggerFactory,
                                             @NotNull final AnalyticsTracker analyticsTracker,
                                             @NotNull final CacheManager cacheManager,
                                             @NotNull final MetricRegistry metricRegistry,
                                             @NotNull final BuildTriggerProperties buildTriggerProperties) {
        this.pluginDescriptor = pluginDescriptor;
        this.analyticsTracker = analyticsTracker;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
        this.buildTriggerProperties = buildTriggerProperties;
        this.buildTriggeringPolicy = triggerFactory.createBuildTrigger(ReleaseCreatedSpec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
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
        return buildTriggeringPolicy;
    }

    @Override
    public PropertiesProcessor getTriggerPropertiesProcessor() {
        return new ReleaseCreatedTriggerPropertiesProcessor(cacheManager, metricRegistry, buildTriggerProperties);
    }

    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("editOctopusReleaseCreatedTrigger.jsp");
    }

    @Override
    public boolean isMultipleTriggersPerBuildTypeAllowed() {
        return true;
    }

    @NotNull
    private CustomAsyncBuildTrigger<ReleaseCreatedSpec> getAsyncBuildTrigger() {
        return getBuildTrigger();
    }

    @NotNull
    private int getPollInterval() {
        return buildTriggerProperties.getPollInterval();
    }

    @NotNull
    private ReleaseCreatedAsyncBuildTrigger getBuildTrigger() {
        return new ReleaseCreatedAsyncBuildTrigger(getDisplayName(), analyticsTracker, cacheManager, metricRegistry, buildTriggerProperties);
    }
}
