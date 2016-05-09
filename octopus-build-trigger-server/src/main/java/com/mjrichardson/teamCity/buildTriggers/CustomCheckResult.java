package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public abstract class CustomCheckResult<TItem> extends CheckResult<TItem> {
    private final UUID correlationId;

    protected CustomCheckResult(UUID correlationId) {
        super();
        this.correlationId = correlationId;
    }

    protected CustomCheckResult(@NotNull Collection<TItem> updated, @NotNull Map<TItem, DetectionException> errors, UUID correlationId) {
        super(updated, errors);
        this.correlationId = correlationId;
    }

    protected CustomCheckResult(@NotNull Throwable generalError, UUID correlationId) {
        super(generalError);
        this.correlationId = correlationId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
