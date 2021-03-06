/*
 * Copyright 2016 Matt Richardson.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * Based on code graciously open sourced by JetBrains s.r.o
 * (http://svn.jetbrains.org/teamcity/plugins/url-build-trigger/trunk/url-build-trigger-server/src/jetbrains/buildServer/buildTriggers/url/UrlBuildTriggerUtil.java)
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

package com.mjrichardson.teamCity.buildTriggers;

public final class BuildTriggerConstants {
    public static final String OCTOPUS_URL = "octopus.build.trigger.url";
    public static final String OCTOPUS_APIKEY = "octopus.build.trigger.apikey";
    public static final String OCTOPUS_PROJECT_ID = "octopus.build.trigger.project.url";
    public static final String OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT = "octopus.build.trigger.only.on.successful.deployment";

    public static final String BUILD_PROPERTY_MACHINE_NAME = "octopus.trigger.machine.name";
    public static final String BUILD_PROPERTY_MACHINE_ID = "octopus.trigger.machine.id";
    public static final String BUILD_PROPERTY_MACHINE_ROLE_IDS = "octopus.trigger.machine.role.ids";
    public static final String BUILD_PROPERTY_MACHINE_ENVIRONMENT_IDS = "octopus.trigger.machine.environment.ids";

    public static final String BUILD_PROPERTY_RELEASE_ID = "octopus.trigger.release.id";
    public static final String BUILD_PROPERTY_RELEASE_VERSION = "octopus.trigger.release.version";
    public static final String BUILD_PROPERTY_RELEASE_PROJECT_ID = "octopus.trigger.release.project.id";

    public static final String BUILD_PROPERTY_DEPLOYMENT_ID = "octopus.trigger.deployment.id";
    public static final String BUILD_PROPERTY_DEPLOYMENT_NAME = "octopus.trigger.deployment.name";
    public static final String BUILD_PROPERTY_DEPLOYMENT_VERSION = "octopus.trigger.deployment.version";
    public static final String BUILD_PROPERTY_DEPLOYMENT_PROJECT_ID = "octopus.trigger.deployment.project.id";
    public static final String BUILD_PROPERTY_DEPLOYMENT_RELEASE_ID = "octopus.trigger.deployment.release.id";
    public static final String BUILD_PROPERTY_DEPLOYMENT_ENVIRONMENT_NAME = "octopus.trigger.deployment.environment.name";
    public static final String BUILD_PROPERTY_DEPLOYMENT_ENVIRONMENT_ID = "octopus.trigger.deployment.environment.id";
    public static final String BUILD_PROPERTY_DEPLOYMENT_SUCCESSFUL = "octopus.trigger.deployment.successful";

    public static final String BUILD_PROPERTY_DEPLOYMENT_PROCESS_VERSION = "octopus.trigger.deployment.process.version";
    public static final String BUILD_PROPERTY_DEPLOYMENT_PROCESS_PROJECT_ID = "octopus.trigger.deployment.process.project.id";
}

