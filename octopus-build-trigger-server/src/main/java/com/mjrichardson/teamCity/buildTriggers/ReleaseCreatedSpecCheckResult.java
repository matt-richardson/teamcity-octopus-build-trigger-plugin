package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class ReleaseCreatedSpecCheckResult extends CheckResult<ReleaseCreatedSpec> {
  private ReleaseCreatedSpecCheckResult() {
    super();
  }

  private ReleaseCreatedSpecCheckResult(@NotNull Collection<ReleaseCreatedSpec> updated, @NotNull Map<ReleaseCreatedSpec, DetectionException> errors) {
    super(updated, errors);
  }

  private ReleaseCreatedSpecCheckResult(@NotNull Throwable generalError) {
    super(generalError);
  }

  @NotNull
  static ReleaseCreatedSpecCheckResult createEmptyResult() {
    return new ReleaseCreatedSpecCheckResult();
  }

  @NotNull
  static ReleaseCreatedSpecCheckResult createUpdatedResult(@NotNull ReleaseCreatedSpec ReleaseCreatedSpec) {
    return new ReleaseCreatedSpecCheckResult(Collections.singleton(ReleaseCreatedSpec), Collections.<ReleaseCreatedSpec, DetectionException>emptyMap());
  }

  @NotNull
  static ReleaseCreatedSpecCheckResult createThrowableResult(@NotNull Throwable throwable) {
    return new ReleaseCreatedSpecCheckResult(throwable);
  }

  @NotNull
  static ReleaseCreatedSpecCheckResult createThrowableResult(@NotNull ReleaseCreatedSpec ReleaseCreatedSpec, @NotNull Throwable throwable) {
    return new ReleaseCreatedSpecCheckResult(Collections.singleton(ReleaseCreatedSpec), Collections.singletonMap(ReleaseCreatedSpec, new DetectionException(throwable.getMessage(), throwable)));
  }

  @NotNull
  static ReleaseCreatedSpecCheckResult createErrorResult(@NotNull String error) {
    return new ReleaseCreatedSpecCheckResult(new BuildTriggerException(error));
  }
}
