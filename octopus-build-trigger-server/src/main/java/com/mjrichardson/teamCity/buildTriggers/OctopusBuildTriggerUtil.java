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

package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.serverSide.TeamCityProperties;

public final class OctopusBuildTriggerUtil {
    public static String OCTOPUS_URL = "octopus.build.trigger.url";
    public static String OCTOPUS_APIKEY = "octopus.build.trigger.apikey";
    public static String OCTOPUS_PROJECT_ID = "octopus.build.trigger.project.url";
    public static String OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT = "octopus.build.trigger.only.on.successful.deployment";

    public static String POLL_INTERVAL_PROP = "octopus.build.trigger.poll.interval.in.seconds";
    public static final Integer DEFAULT_POLL_INTERVAL_IN_SECONDS = 30;

    private static final String CONNECTION_TIMEOUT_PROP = "octopus.build.trigger.connection.timeout.in.milliseconds";
    private static final Integer DEFAULT_CONNECTION_TIMEOUT_IN_MILLISECONDS = 60 * 1000;

    public static Integer getConnectionTimeoutInMilliseconds() {
        return TeamCityProperties.getInteger(CONNECTION_TIMEOUT_PROP, DEFAULT_CONNECTION_TIMEOUT_IN_MILLISECONDS);
    }
}
