package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.async.AsyncBuildTriggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeAsyncBuildTriggerFactory implements AsyncBuildTriggerFactory {

    private final BuildTriggeringPolicy fakeBuildTriggeringPolicy;

    public FakeAsyncBuildTriggerFactory() {
        this(new FakeBuildTriggeringPolicy());
    }

    public FakeAsyncBuildTriggerFactory(BuildTriggeringPolicy fakeBuildTriggeringPolicy) {
        this.fakeBuildTriggeringPolicy = fakeBuildTriggeringPolicy;
    }

    @NotNull
    @Override
    public <TItem> BuildTriggeringPolicy createBuildTrigger(@NotNull Class<TItem> aClass, @NotNull AsyncBuildTrigger<TItem> asyncBuildTrigger, @NotNull Logger logger, @Nullable Integer integer) {
        return fakeBuildTriggeringPolicy;
    }
}
