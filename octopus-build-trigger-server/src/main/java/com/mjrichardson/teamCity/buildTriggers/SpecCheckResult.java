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

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class SpecCheckResult extends CheckResult<Spec> {
  private SpecCheckResult() {
    super();
  }

  private SpecCheckResult(@NotNull Collection<Spec> updated, @NotNull Map<Spec, DetectionException> errors) {
    super(updated, errors);
  }

  private SpecCheckResult(@NotNull Throwable generalError) {
    super(generalError);
  }

  @NotNull
  static SpecCheckResult createEmptyResult() {
    return new SpecCheckResult();
  }

  @NotNull
  static SpecCheckResult createUpdatedResult(@NotNull Spec spec) {
    return new SpecCheckResult(Collections.singleton(spec), Collections.<Spec, DetectionException>emptyMap());
  }

  @NotNull
  static SpecCheckResult createThrowableResult(@NotNull Throwable throwable) {
    return new SpecCheckResult(throwable);
  }

  @NotNull
  static SpecCheckResult createThrowableResult(@NotNull Spec spec, @NotNull Throwable throwable) {
    return new SpecCheckResult(Collections.singleton(spec), Collections.singletonMap(spec, new DetectionException(throwable.getMessage(), throwable)));
  }

  @NotNull
  static SpecCheckResult createErrorResult(@NotNull String error) {
    return new SpecCheckResult(new BuildTriggerException(error));
  }
}
