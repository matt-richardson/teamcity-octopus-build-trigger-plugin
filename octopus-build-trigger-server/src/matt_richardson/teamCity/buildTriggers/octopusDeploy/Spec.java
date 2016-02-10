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

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Spec {
  @NotNull
  private final String url;
  @NotNull
  private final String project;
  @Nullable
  private final Boolean wasSuccessful;

  Spec(@NotNull String url, @NotNull String project) {
    this(url, project, null);
  }

  Spec(@NotNull String url, @NotNull String project, Boolean wasSuccessful) {
    this.url = url;
    this.project = project;
    this.wasSuccessful = wasSuccessful;
  }

  @NotNull
  String getUrl() {
    return this.url;
  }

  @NotNull
  String getProject() {
    return this.project;
  }

  public boolean getWasSuccessful() {
    return this.wasSuccessful;
  }
}
