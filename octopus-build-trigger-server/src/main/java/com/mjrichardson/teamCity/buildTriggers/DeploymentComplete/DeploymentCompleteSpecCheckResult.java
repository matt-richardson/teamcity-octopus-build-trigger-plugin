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

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class DeploymentCompleteSpecCheckResult extends CheckResult<DeploymentCompleteSpec> {
    private DeploymentCompleteSpecCheckResult() {
        super();
    }

    private DeploymentCompleteSpecCheckResult(@NotNull Collection<DeploymentCompleteSpec> updated, @NotNull Map<DeploymentCompleteSpec, DetectionException> errors) {
        super(updated, errors);
    }

    private DeploymentCompleteSpecCheckResult(@NotNull Throwable generalError) {
        super(generalError);
    }

    @NotNull
    static DeploymentCompleteSpecCheckResult createEmptyResult() {
        return new DeploymentCompleteSpecCheckResult();
    }

    @NotNull
    static DeploymentCompleteSpecCheckResult createUpdatedResult(@NotNull DeploymentCompleteSpec deploymentCompleteSpec) {
        return new DeploymentCompleteSpecCheckResult(Collections.singleton(deploymentCompleteSpec), Collections.emptyMap());
    }

    @NotNull
    static DeploymentCompleteSpecCheckResult createThrowableResult(@NotNull Throwable throwable) {
        return new DeploymentCompleteSpecCheckResult(throwable);
    }

    @NotNull
    static DeploymentCompleteSpecCheckResult createErrorResult(@NotNull String error) {
        return new DeploymentCompleteSpecCheckResult(new BuildTriggerException(error));
    }
}
