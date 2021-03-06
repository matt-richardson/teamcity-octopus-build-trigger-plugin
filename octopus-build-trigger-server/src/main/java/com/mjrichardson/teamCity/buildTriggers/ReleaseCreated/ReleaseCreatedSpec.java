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

package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import org.jetbrains.annotations.NotNull;

class ReleaseCreatedSpec {
    @NotNull
    private final String url;
    @NotNull
    final String projectId;
    @NotNull
    final String version;
    @NotNull
    final String releaseId;

    ReleaseCreatedSpec(@NotNull String url, @NotNull Release release) {
        this(url, release.projectId, release.version, release.releaseId);
    }

    private ReleaseCreatedSpec(@NotNull String url, @NotNull String projectId, @NotNull String version, @NotNull String releaseId) {
        this.url = url;
        this.projectId = projectId;
        this.version = version;
        this.releaseId = releaseId;
    }

    public String getRequestorString() {
        return String.format("Release %s of project %s created on %s", version, projectId, url);
    }

    @Override
    public String toString() {
        return "{ " +
                  "url: '" + url + "', " +
                  "projectId: '" + projectId + "', " +
                  "version: '" + version + "', " +
                  "releaseId: '" + releaseId + "'" +
               " }";
    }
}
