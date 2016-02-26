package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//todo: should this be part of the ReleaseCreatedAsyncBuildTrigger class instead?
//todo: refactor
//todo: add tests
class ReleaseCreatedTriggerPropertiesProcessor implements PropertiesProcessor {
  @NotNull
  private static final Logger LOG = Logger.getInstance(ReleaseCreatedBuildTriggerService.class.getName());

  public Collection<InvalidProperty> process(Map<String, String> properties) {
    final ArrayList<InvalidProperty> invalidProps = new ArrayList<InvalidProperty>();
    final String url = properties.get(OctopusBuildTriggerUtil.OCTOPUS_URL);
    if (StringUtil.isEmptyOrSpaces(url)) {
      invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, "URL must be specified"));
    }
    final String apiKey = properties.get(OctopusBuildTriggerUtil.OCTOPUS_APIKEY);
    if (StringUtil.isEmptyOrSpaces(apiKey)) {
      invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_APIKEY, "API Key must be specified"));
    }
    final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix

    if (invalidProps.size() == 0) {
      final OctopusConnectivityChecker connectivityChecker;
      try {
        connectivityChecker = new OctopusConnectivityChecker(url, apiKey, connectionTimeout);
        final String err = connectivityChecker.checkOctopusConnectivity();
        if (StringUtil.isNotEmpty(err)) {
          invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, err));
        }
        final String project = properties.get(OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID);
        if (StringUtil.isEmptyOrSpaces(project)) {
          invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID, "Project must be specified")); //todo: change to use dropdown / name
        }
      } catch (Exception e) {
        invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, e.toString()));
      }
    }
    return invalidProps;
  }
}
