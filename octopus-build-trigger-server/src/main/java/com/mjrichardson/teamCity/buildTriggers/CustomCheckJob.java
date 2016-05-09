package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class CustomCheckJob<TItem> implements CheckJob<TItem> {
    @NotNull
    @Override
    @Deprecated
    //override this method - we dont actually use it, as we have overridden the caller to call the other method below
    public final CheckResult<TItem> perform() {
        throw new NotImplementedException("This shouldn't be called - use perform(UUID)");
    }

    @NotNull
    public abstract CheckResult<TItem> perform(UUID correlationId);

    @Override
    public final boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        //we always return false here - the AsyncPolledBuildTrigger class handles whether we are busy or not
        //also, this is inverted, the method should be preventSchedule or something
        return false;
    }
}
