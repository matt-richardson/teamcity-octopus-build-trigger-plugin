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
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants.*;

class ReleaseCreatedCheckJob extends CustomCheckJob<ReleaseCreatedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedCheckJob.class.getName());

    private final ReleasesProviderFactory releasesProviderFactory;
    private final String displayName;
    private final SBuildType buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;
    private final BuildTriggerProperties buildTriggerProperties;

    public ReleaseCreatedCheckJob(String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, CacheManager cacheManager, MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties) {
        this(new ReleasesProviderFactory(analyticsTracker, cacheManager, metricRegistry), displayName, buildType, dataStorage, properties, analyticsTracker, buildTriggerProperties);
    }

    public ReleaseCreatedCheckJob(ReleasesProviderFactory releasesProviderFactory, String displayName, SBuildType buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker, BuildTriggerProperties buildTriggerProperties) {
        this.releasesProviderFactory = releasesProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
        this.buildTriggerProperties = buildTriggerProperties;
    }

    @NotNull
    CheckResult<ReleaseCreatedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage, UUID correlationId) {
        LOG.debug(String.format("%s: Checking for new releases for project %s on server %s", correlationId, octopusProject, octopusUrl));
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Release oldRelease = Release.Parse(oldStoredData);

            ReleasesProvider provider = releasesProviderFactory.getProvider(octopusUrl, octopusApiKey, buildTriggerProperties);
            final Releases newReleases = provider.getReleases(octopusProject, oldRelease, correlationId);

            //only store that one release has happened here, not multiple.
            //otherwise, we could inadvertently miss releases
            final Release newRelease = newReleases.getNextRelease(oldRelease);
            final String newStoredData = newRelease.toString();

            //do not trigger build after first adding trigger (oldReleases == null)
            if (oldStoredData == null) {
                //store the latest releases, so we only trigger for new releases from this point in time
                dataStorage.putValue(dataStorageKey, newReleases.getLatestRelease().toString());
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.ReleaseCreatedTrigger, AnalyticsTracker.EventAction.TriggerAdded, correlationId);

                LOG.debug(String.format("%s: No previous releases known for server %s, project %s: null -> %s", correlationId, octopusUrl, octopusProject, newStoredData));
                return ReleaseCreatedSpecCheckResult.createEmptyResult(correlationId);
            }

            if (!newRelease.equals(oldRelease)) {
                dataStorage.putValue(dataStorageKey, newStoredData);
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.ReleaseCreatedTrigger, AnalyticsTracker.EventAction.BuildTriggered, correlationId);

                LOG.info(String.format("%s: New release %s created on %s for project %s: %s -> %s", correlationId, newRelease.version, octopusUrl, octopusProject, oldStoredData, newStoredData));
                final ReleaseCreatedSpec releaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, newRelease);
                return ReleaseCreatedSpecCheckResult.createUpdatedResult(releaseCreatedSpec, correlationId);
            }

            LOG.debug(String.format("%s: oldStoredData was '%s'", correlationId, oldStoredData));
            LOG.debug(String.format("%s: newStoredData was '%s'", correlationId, newStoredData));
            LOG.info(String.format("%s: No new releases on '%s' for project '%s'", correlationId, octopusUrl, octopusProject));
            return ReleaseCreatedSpecCheckResult.createEmptyResult(correlationId);

        } catch (Exception e) {
            LOG.error(String.format("%s: Failed to check for new releases created", correlationId), e);

            analyticsTracker.postException(e, correlationId);

            return ReleaseCreatedSpecCheckResult.createThrowableResult(e, correlationId);
        }
    }

    @NotNull
    public CheckResult<ReleaseCreatedSpec> perform(UUID correlationId) {
        ValueResolver resolver = this.buildType.getValueResolver();
        final String octopusUrl = resolveValue(resolver, props.get(OCTOPUS_URL));
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, buildType), correlationId);
        }

        final String octopusApiKey = resolveValue(resolver, props.get(OCTOPUS_APIKEY));
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, buildType), correlationId);
        }

        final String octopusProject = resolveValue(resolver, props.get(OCTOPUS_PROJECT_ID));
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, buildType), correlationId);
        }

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, dataStorage, correlationId);
    }
}
