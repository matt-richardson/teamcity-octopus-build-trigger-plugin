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

class ReleaseCreatedCheckJob implements CheckJob<ReleaseCreatedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedCheckJob.class.getName());

    private final ReleasesProviderFactory releasesProviderFactory;
    private final String displayName;
    private final String buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;

    public ReleaseCreatedCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this(new ReleasesProviderFactory(analyticsTracker), displayName, buildType, dataStorage, properties, analyticsTracker);
    }

    public ReleaseCreatedCheckJob(ReleasesProviderFactory releasesProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this.releasesProviderFactory = releasesProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
    }

    @NotNull
    CheckResult<ReleaseCreatedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage) {
        LOG.debug("Checking for new releases for project " + octopusProject + " on server " + octopusUrl);
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Release oldRelease = Release.Parse(oldStoredData);
            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();

            ReleasesProvider provider = releasesProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeoutInMilliseconds);
            final Releases newReleases = provider.getReleases(octopusProject, oldRelease);

            //only store that one release has happened here, not multiple.
            //otherwise, we could inadvertently miss releases
            final Release newRelease = newReleases.getNextRelease(oldRelease);
            final String newStoredData = newRelease.toString();

            if (!newRelease.toString().equals(oldStoredData)) {
                //do not trigger build after first adding trigger (oldReleases == null)
                if (oldStoredData == null) {
                    //store all existing releases, so we only trigger for new releases from this point in time
                    dataStorage.putValue(dataStorageKey, newReleases.toString());
                    analyticsTracker.postEvent(AnalyticsTracker.EventCategory.ReleaseCreatedTrigger, AnalyticsTracker.EventAction.TriggerAdded);

                    LOG.debug("No previous releases known for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
                    return ReleaseCreatedSpecCheckResult.createEmptyResult();
                }

                dataStorage.putValue(dataStorageKey, newStoredData);
                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.ReleaseCreatedTrigger, AnalyticsTracker.EventAction.BuildTriggered);

                LOG.info("New release " + newRelease.version + " created on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
                final ReleaseCreatedSpec releaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, newRelease);
                return ReleaseCreatedSpecCheckResult.createUpdatedResult(releaseCreatedSpec);
            }

            LOG.debug("oldStoredData was '" + oldStoredData + "'");
            LOG.debug("newStoredData was '" + newStoredData + "'");
            LOG.info("No new releases on '" + octopusUrl + "' for project '" + octopusProject + "'");
            return ReleaseCreatedSpecCheckResult.createEmptyResult();

        } catch (Exception e) {
            LOG.error("Failed to check for new releases created", e);

            analyticsTracker.postException(e);

            return ReleaseCreatedSpecCheckResult.createThrowableResult(e);
        }
    }

    @NotNull
    public CheckResult<ReleaseCreatedSpec> perform() {

        final String octopusUrl = props.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, buildType));
        }

        final String octopusApiKey = props.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, buildType));
        }

        final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
        if (StringUtil.isEmptyOrSpaces(octopusProject)) {
            return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
                    displayName, buildType));
        }

        return getCheckResult(octopusUrl, octopusApiKey, octopusProject, dataStorage);
    }

    public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        //we always return false here - the AsyncPolledBuildTrigger class handles whether we are busy or not
        //also, this is inverted, the method should be preventSchedule or something
        return false;
    }
}
