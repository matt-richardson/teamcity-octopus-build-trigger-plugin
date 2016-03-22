package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

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

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_APIKEY;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_URL;

class MachineAddedCheckJob implements CheckJob<MachineAddedSpec> {
    @NotNull
    private static final Logger LOG = Logger.getInstance(MachineAddedCheckJob.class.getName());

    private final MachinesProviderFactory MachinesProviderFactory;
    private final String displayName;
    private final String buildType;
    private final CustomDataStorage dataStorage;
    private final Map<String, String> props;
    private final AnalyticsTracker analyticsTracker;

    public MachineAddedCheckJob(String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this(new MachinesProviderFactory(), displayName, buildType, dataStorage, properties, analyticsTracker);
    }

    public MachineAddedCheckJob(MachinesProviderFactory MachinesProviderFactory, String displayName, String buildType, CustomDataStorage dataStorage, Map<String, String> properties, AnalyticsTracker analyticsTracker) {
        this.MachinesProviderFactory = MachinesProviderFactory;
        this.displayName = displayName;
        this.buildType = buildType;
        this.dataStorage = dataStorage;
        this.props = properties;
        this.analyticsTracker = analyticsTracker;
    }

    @NotNull
    CheckResult<MachineAddedSpec> getCheckResult(String octopusUrl, String octopusApiKey, CustomDataStorage dataStorage) {
        LOG.debug("Checking for new machines for on server " + octopusUrl);
        final String dataStorageKey = (displayName + "|" + octopusUrl).toLowerCase();

        try {
            String oldStoredData = dataStorage.getValue(dataStorageKey);
            final Machines oldMachines = Machines.Parse(oldStoredData);
            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();

            MachinesProvider provider = MachinesProviderFactory.getProvider(octopusUrl, octopusApiKey, connectionTimeoutInMilliseconds);
            final Machines newMachines = provider.getMachines();

            //only store that one machine was added here, not multiple.
            //otherwise, we could inadvertently miss new machines
            final Machine newMachine = newMachines.getNextMachine(oldMachines);
            final Machines trimmedMachines = Machines.Parse(oldStoredData);
            trimmedMachines.add(newMachine);
            final String newStoredData = trimmedMachines.toString();

            if (!newStoredData.equals(oldStoredData)) {
                dataStorage.putValue(dataStorageKey, newStoredData);

                //todo: see if its possible to to check the property on the context that says whether its new?
                //http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/buildTriggers/PolledTriggerContext.html#getPreviousCallTime()
                //do not trigger build after first adding trigger (oldMachines == null)
                if (oldStoredData == null) {
                    analyticsTracker.postEvent(AnalyticsTracker.EventCategory.MachineAddedTrigger, AnalyticsTracker.EventAction.TriggerAdded);

                    LOG.debug("No previously known machines known for server " + octopusUrl + ": null" + " -> " + newStoredData);
                    return MachineAddedSpecCheckResult.createEmptyResult();
                }

                analyticsTracker.postEvent(AnalyticsTracker.EventCategory.MachineAddedTrigger, AnalyticsTracker.EventAction.BuildTriggered);

                LOG.info("New Machine " + newMachine.name + " created on " + octopusUrl + ": " + oldStoredData + " -> " + newStoredData);
                final MachineAddedSpec MachineAddedSpec = new MachineAddedSpec(octopusUrl, newMachine);
                //todo: investigate passing multiple bits to createUpdatedResult()
                return MachineAddedSpecCheckResult.createUpdatedResult(MachineAddedSpec);
            }

            LOG.debug("oldStoredData was '" + oldStoredData + "'");
            LOG.debug("newStoredData was '" + newStoredData + "'");
            LOG.info("No new machines on '" + octopusUrl + "'");

            return MachineAddedSpecCheckResult.createEmptyResult();

        } catch (Exception e) {
            LOG.error("Failed to check for new machines added", e);

            analyticsTracker.postException(e);

            return MachineAddedSpecCheckResult.createThrowableResult(e);
        }
    }

    @NotNull
    public CheckResult<MachineAddedSpec> perform() {

        final String octopusUrl = props.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
            return MachineAddedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
                    displayName, buildType));
        }

        final String octopusApiKey = props.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
            return MachineAddedSpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
                    displayName, buildType));
        }

        return getCheckResult(octopusUrl, octopusApiKey, dataStorage);
    }

    public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        //we always return false here - the AsyncPolledBuildTrigger class handles whether we are busy or not
        //also, this is inverted, the method should be preventSchedule or something
        return false;
    }
}
