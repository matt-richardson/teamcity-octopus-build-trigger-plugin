package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityCheckerFactory;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class MachineAddedTriggerPropertiesProcessor implements PropertiesProcessor {

    private final OctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory;

    public MachineAddedTriggerPropertiesProcessor() {
        this(new OctopusConnectivityCheckerFactory());
    }

    public MachineAddedTriggerPropertiesProcessor(OctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory) {
        this.octopusConnectivityCheckerFactory = octopusConnectivityCheckerFactory;
    }

    public Collection<InvalidProperty> process(Map<String, String> properties) {
        final ArrayList<InvalidProperty> invalidProps = new ArrayList<>();

        final String url = properties.get(OctopusBuildTriggerUtil.OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(url)) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, "URL must be specified"));
        }

        final String apiKey = properties.get(OctopusBuildTriggerUtil.OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(apiKey)) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_APIKEY, "API Key must be specified"));
        }

        if (invalidProps.size() == 0) {
            checkConnectivity(properties, invalidProps, url, apiKey);
        }
        return invalidProps;
    }

    //todo: remove unused param (check elsewhere as well)
    private void checkConnectivity(Map<String, String> properties, ArrayList<InvalidProperty> invalidProps, String url, String apiKey) {
        try {
            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();
            final OctopusConnectivityChecker connectivityChecker = octopusConnectivityCheckerFactory.create(url, apiKey, connectionTimeoutInMilliseconds);

            final String err = connectivityChecker.checkOctopusConnectivity();
            if (StringUtil.isNotEmpty(err)) {
                invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, err));
            }
        } catch (Exception e) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, e.getMessage()));
        }
    }
}
