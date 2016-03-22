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

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class MachineAddedSpecCheckResult extends CheckResult<MachineAddedSpec> {
    private MachineAddedSpecCheckResult() {
        super();
    }

    private MachineAddedSpecCheckResult(@NotNull Collection<MachineAddedSpec> updated, @NotNull Map<MachineAddedSpec, DetectionException> errors) {
        super(updated, errors);
    }

    private MachineAddedSpecCheckResult(@NotNull Throwable generalError) {
        super(generalError);
    }

    @NotNull
    static MachineAddedSpecCheckResult createEmptyResult() {
        return new MachineAddedSpecCheckResult();
    }

    @NotNull
    static MachineAddedSpecCheckResult createUpdatedResult(@NotNull MachineAddedSpec MachineAddedSpec) {
        return new MachineAddedSpecCheckResult(Collections.singleton(MachineAddedSpec), Collections.<MachineAddedSpec, DetectionException>emptyMap());
    }

    @NotNull
    static MachineAddedSpecCheckResult createThrowableResult(@NotNull Throwable throwable) {
        return new MachineAddedSpecCheckResult(throwable);
    }

    @NotNull
    static MachineAddedSpecCheckResult createErrorResult(@NotNull String error) {
        return new MachineAddedSpecCheckResult(new BuildTriggerException(error));
    }
}
