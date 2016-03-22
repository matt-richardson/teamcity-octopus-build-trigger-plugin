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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DeploymentCompleteSpec {
    @NotNull
    private final String url;
    @NotNull
    final String deploymentId;
    @NotNull
    final String version;
    @NotNull
    final String releaseId;
    @NotNull
    final String projectId;
    @Nullable
    final Boolean wasSuccessful;
    @Nullable
    final String environmentId;

    DeploymentCompleteSpec(@NotNull String url, @NotNull String projectId) {
        this(url, projectId, null, null, null, null, null);
    }

    DeploymentCompleteSpec(@NotNull String url, @NotNull String projectId, @NotNull Environment environment) {
        this(url, projectId, environment.environmentId, environment.wasLatestDeploymentSuccessful(),
                environment.deploymentId, environment.version, environment.releaseId);
    }

    DeploymentCompleteSpec(@NotNull String url, @NotNull String projectId, @Nullable String environmentId,
                           @Nullable Boolean wasSuccessful) {
        this(url, projectId, environmentId, wasSuccessful, null, null, null);
    }

    DeploymentCompleteSpec(@NotNull String url, @NotNull String projectId, @Nullable String environmentId,
                           @Nullable Boolean wasSuccessful, @Nullable String deploymentId, @Nullable String version,
                           @Nullable String releaseId) {
        this.url = url;
        this.projectId = projectId;
        this.wasSuccessful = wasSuccessful;
        this.environmentId = environmentId;
        this.deploymentId = deploymentId;
        this.version = version;
        this.releaseId = releaseId;
    }

    public String getRequestorString() {
        if (environmentId == null || environmentId == null)
            return String.format("Unsuccessful attempt to get deployments for %s on %s", projectId, url);
        if (wasSuccessful)
            return String.format("Successful deployment of %s to %s on %s", projectId, environmentId, url);
        return String.format("Deployment of %s to %s on %s", projectId, environmentId, url);
    }
}
