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
