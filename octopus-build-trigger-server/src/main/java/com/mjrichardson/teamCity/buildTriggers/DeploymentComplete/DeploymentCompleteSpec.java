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
  private final String project;
  @Nullable
  private final Boolean wasSuccessful;
  @Nullable
  private final String environmentId;

  DeploymentCompleteSpec(@NotNull String url, @NotNull String project) {
    this(url, project, null, null);
  }

  DeploymentCompleteSpec(@NotNull String url, @NotNull String project, @Nullable String environmentId, @Nullable Boolean wasSuccessful) {
    this.url = url;
    this.project = project;
    this.wasSuccessful = wasSuccessful;
    this.environmentId = environmentId;
  }

  public String getRequestorString() {
    if (wasSuccessful == null)
      return String.format("Unsuccessful attempt to get deployments for %s on %s", project, url);
    if (wasSuccessful)
      return String.format("Successful deployment of %s to %s on %s", project, environmentId, url);
    return String.format("Deployment of %s on %s", project, url);
  }
}
