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

import com.mjrichardson.teamCity.buildTriggers.CustomCheckResult;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

class ReleaseCreatedSpecCheckResult extends CustomCheckResult<ReleaseCreatedSpec> {
    private ReleaseCreatedSpecCheckResult(UUID correlationId) {
        super(correlationId);
    }

    private ReleaseCreatedSpecCheckResult(@NotNull Collection<ReleaseCreatedSpec> updated, @NotNull Map<ReleaseCreatedSpec, DetectionException> errors, UUID correlationId) {
        super(updated, errors, correlationId);
    }

    private ReleaseCreatedSpecCheckResult(@NotNull Throwable generalError, UUID correlationId) {
        super(generalError, correlationId);
    }

    @NotNull
    static ReleaseCreatedSpecCheckResult createEmptyResult(UUID correlationId) {
        return new ReleaseCreatedSpecCheckResult(correlationId);
    }

    @NotNull
    static ReleaseCreatedSpecCheckResult createUpdatedResult(@NotNull ReleaseCreatedSpec ReleaseCreatedSpec, UUID correlationId) {
        return new ReleaseCreatedSpecCheckResult(Collections.singleton(ReleaseCreatedSpec), Collections.<ReleaseCreatedSpec, DetectionException>emptyMap(), correlationId);
    }

    @NotNull
    static ReleaseCreatedSpecCheckResult createThrowableResult(@NotNull Throwable throwable, UUID correlationId) {
        return new ReleaseCreatedSpecCheckResult(throwable, correlationId);
    }

    @NotNull
    static ReleaseCreatedSpecCheckResult createErrorResult(@NotNull String error, UUID correlationId) {
        return new ReleaseCreatedSpecCheckResult(new BuildTriggerException(error), correlationId);
    }
}
