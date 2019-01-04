package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.buildTriggers.async.*;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildType;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public abstract class CustomAsyncBuildTrigger<TItem> implements AsyncBuildTrigger<TItem> {
    public abstract Map<String, String> getProperties(TItem item);
    public abstract CheckResult<TItem> createCrashOnSubmitResult(@NotNull Throwable throwable, UUID correlationId);
    public abstract int getPollIntervalInMilliseconds();
    public abstract CustomCheckJob<TItem> createJob(@NotNull SBuildType buildType, @NotNull CustomDataStorage dataStorage, @NotNull Map<String, String> properties, @NotNull UUID correlationId) throws CheckJobCreationException;

    //override these methods - we dont actually use them, as we have overridden the caller to call the other methods above
    @Deprecated
    public final CheckJob<TItem> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        throw new NotImplementedException("This shouldn't be called - use createJob(String, CustomDataStorage, Map<String, String>, UUID)");
    }

    @Deprecated
    public final int getPollInterval(@NotNull AsyncTriggerParameters var1) {
        throw new NotImplementedException("This shouldn't be called - use getPollIntervalInMilliseconds()");
    }

    @Deprecated
    public final CheckResult<TItem> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        throw new NotImplementedException("This shouldn't be called - use createCrashOnSubmitResult(Throwable, UUID)");
    }
}
