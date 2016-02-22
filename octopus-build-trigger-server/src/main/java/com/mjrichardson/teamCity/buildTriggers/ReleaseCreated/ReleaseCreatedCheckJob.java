package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.AsyncTriggerParameters;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

//todo: this needs tests
class ReleaseCreatedCheckJob implements CheckJob<ReleaseCreatedSpec> {
  @NotNull
  private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTrigger.class.getName());

  private final AsyncTriggerParameters asyncTriggerParameters;
  private final String displayName;

  public ReleaseCreatedCheckJob(AsyncTriggerParameters asyncTriggerParameters, String displayName) {
    this.asyncTriggerParameters = asyncTriggerParameters;
    this.displayName = displayName;
  }

  @NotNull
  CheckResult<ReleaseCreatedSpec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject, CustomDataStorage dataStorage) {
    LOG.debug("Checking for new releases for project " + octopusProject + " on server " + octopusUrl);
    final String dataStorageKey = (displayName + "|" + octopusUrl + "|" + octopusProject).toLowerCase();

    try {
      final String oldStoredData = dataStorage.getValue(dataStorageKey);
      final Releases oldReleases = new Releases(oldStoredData);
      final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix

      ReleasesProvider provider = new ReleasesProvider(octopusUrl, octopusApiKey, connectionTimeout, LOG);
      final Releases newReleases = provider.getReleases(octopusProject, oldReleases);

      //only store that one release has happened here, not multiple .
      //otherwise, we could inadvertently miss releases
      final Releases newStoredData = newReleases.trimToOnlyHaveMaximumOneChangedRelease(oldReleases);

      if (!newReleases.toString().equals(oldReleases.toString())) {
        dataStorage.putValue(dataStorageKey, newStoredData.toString());

        //todo: see if its possible to to check the property on the context that says whether its new?
        //http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/buildTriggers/PolledTriggerContext.html#getPreviousCallTime()
        //do not trigger build after first adding trigger (oldReleases == null)
        if (oldReleases.isEmpty()) {
          LOG.debug("No previous releases known for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
          return ReleaseCreatedSpecCheckResult.createEmptyResult();
        }

        Release release = newReleases.getChangedRelease(oldReleases);
        LOG.info("New release " + release.version + " created on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
        final ReleaseCreatedSpec releaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, octopusProject, release.version);
        //todo: investigate passing multiple bits to createUpdatedResult()
        return ReleaseCreatedSpecCheckResult.createUpdatedResult(releaseCreatedSpec);
      }

      LOG.info("No new deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
      return ReleaseCreatedSpecCheckResult.createEmptyResult();

    } catch (Exception e) {
      final ReleaseCreatedSpec ReleaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, octopusProject);
      return ReleaseCreatedSpecCheckResult.createThrowableResult(ReleaseCreatedSpec, e);
    } catch (ReleasesProviderException e) {
      final ReleaseCreatedSpec ReleaseCreatedSpec = new ReleaseCreatedSpec(octopusUrl, octopusProject);
      return ReleaseCreatedSpecCheckResult.createThrowableResult(ReleaseCreatedSpec, e);
    }
  }

  @NotNull
  public CheckResult<ReleaseCreatedSpec> perform() {
    final Map<String, String> props = asyncTriggerParameters.getTriggerDescriptor().getProperties();

    final String octopusUrl = props.get(OCTOPUS_URL);
    if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
      return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
              displayName, asyncTriggerParameters.getBuildType()));
    }

    final String octopusApiKey = props.get(OCTOPUS_APIKEY);
    if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
      return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
              displayName, asyncTriggerParameters.getBuildType()));
    }

    final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
    if (StringUtil.isEmptyOrSpaces(octopusProject)) {
      return ReleaseCreatedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
              displayName, asyncTriggerParameters.getBuildType()));
    }

    return getCheckResult(octopusUrl, octopusApiKey, octopusProject, asyncTriggerParameters.getCustomDataStorage());
  }

  public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
    return false;
  }
}
