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
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//todo: should this be part of the DeploymentCompleteAsyncBuildTrigger class instead?
class DeploymentCompleteTriggerPropertiesProcessor implements PropertiesProcessor {
  @NotNull
  private static final Logger LOG = Logger.getInstance(DeploymentCompleteBuildTrigger.class.getName());

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
      final DeploymentsProvider provider;
      try {
        provider = new DeploymentsProvider(url, apiKey, connectionTimeout, LOG);
        final String err = provider.checkOctopusConnectivity();
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
