package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

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

class ReleaseCreatedCheckJob implements CheckJob<ReleaseCreatedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleaseCreatedCheckJob.class.getName());

    private final ReleasesProviderFactory releasesProviderFactory;
    private final String displayName;
    private final String buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;

    public ReleaseCreatedCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties) {
        this(new ReleasesProviderFactory(), displayName, buildType, dataStorage, properties);
    }

    public ReleaseCreatedCheckJob(ReleasesProviderFactory releasesProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties) {
        this.releasesProviderFactory = releasesProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
    }

    @NotNull
    CheckResult<ReleaseCreatedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage) {
        LOG.debug("Checking for new releases for project " + octopusProject + " on server " + octopusUrl);
        final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

        try {
            String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Release oldRelease = Release.Parse(oldStoredData);
            final Integer connectionTimeout = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();

            ReleasesProvider provider = releasesProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeout);
            final Releases newReleases = provider.getReleases(octopusProject, oldRelease);

            //only store that one release has happened here, not multiple.
            //otherwise, we could inadvertently miss releases
            final Release newRelease = newReleases.getNextRelease(oldRelease);
            final String newStoredData = newRelease.toString();

            if (!newRelease.toString().equals(oldStoredData)) {
                dataStorage.putValue(dataStorageKey, newStoredData);

                //todo: see if its possible to to check the property on the context that says whether its new?
                //http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/buildTriggers/PolledTriggerContext.html#getPreviousCallTime()
                //do not trigger build after first adding trigger (oldReleases == null)
                if (oldStoredData == null) {
                    LOG.debug("No previous releases known for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
                    return ReleaseCreatedSpecCheckResult.createEmptyResult();
                }

                LOG.info("New release " + newRelease.version + " created on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
                final ReleaseCreatedSpec releaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, octopusProject, newRelease.version);
                //todo: investigate passing multiple bits to createUpdatedResult()
                return ReleaseCreatedSpecCheckResult.createUpdatedResult(releaseCreatedSpec);
            }

            LOG.info("No new deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
            return ReleaseCreatedSpecCheckResult.createEmptyResult();

        } catch (Exception e) {
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
        return false;
    }
}
