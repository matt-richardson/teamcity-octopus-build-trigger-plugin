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

package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.codahale.metrics.MetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.CacheManager;
import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityCheckerFactory;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

class MachineAddedTriggerPropertiesProcessor implements PropertiesProcessor {

    private final OctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory;
    private MetricRegistry metricRegistry;

    public MachineAddedTriggerPropertiesProcessor(CacheManager cacheManager, MetricRegistry metricRegistry) {
        this(new OctopusConnectivityCheckerFactory(cacheManager));
        this.metricRegistry = metricRegistry;
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
            UUID correlationId = UUID.randomUUID();
            checkConnectivity(invalidProps, url, apiKey, correlationId);
        }
        return invalidProps;
    }

    private void checkConnectivity(ArrayList<InvalidProperty> invalidProps, String url, String apiKey, UUID correlationId) {
        try {
            final Integer connectionTimeoutInMilliseconds = OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds();
            final OctopusConnectivityChecker connectivityChecker = octopusConnectivityCheckerFactory.create(url, apiKey, connectionTimeoutInMilliseconds, metricRegistry);

            final String err = connectivityChecker.checkOctopusConnectivity(correlationId);
            if (StringUtil.isNotEmpty(err)) {
                invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, err));
            }
        } catch (Exception e) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, e.getMessage()));
        }
    }
}
