package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTrigger;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTriggerFactory;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeAsyncBuildTriggerFactory implements CustomAsyncBuildTriggerFactory {

    private final BuildTriggeringPolicy fakeBuildTriggeringPolicy;

    public FakeAsyncBuildTriggerFactory() {
        this(new FakeBuildTriggeringPolicy());
    }

    public FakeAsyncBuildTriggerFactory(BuildTriggeringPolicy fakeBuildTriggeringPolicy) {
        this.fakeBuildTriggeringPolicy = fakeBuildTriggeringPolicy;
    }

    @NotNull
    @Override
    public <TItem> BuildTriggeringPolicy createBuildTrigger(@NotNull Class<TItem> aClass, @NotNull CustomAsyncBuildTrigger<TItem> trigger, @NotNull Logger logger, @Nullable Integer invocationInterval) {
        return fakeBuildTriggeringPolicy;
    }
}
