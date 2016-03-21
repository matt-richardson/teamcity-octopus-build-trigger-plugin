package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomAsyncBuildTriggerFactory {
    <TItem> BuildTriggeringPolicy createBuildTrigger(@NotNull Class<TItem> itemClazz, @NotNull CustomAsyncBuildTrigger<TItem> trigger, @NotNull Logger logger, @Nullable Integer invocationInterval) ;
}
