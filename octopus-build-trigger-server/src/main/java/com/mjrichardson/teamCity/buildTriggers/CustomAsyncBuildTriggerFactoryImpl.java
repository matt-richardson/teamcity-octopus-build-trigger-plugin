package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.buildTriggers.async.JobStatusStorageHolder;
import jetbrains.buildServer.buildTriggers.async.impl.AsyncBuildTriggerFactoryImpl;
import jetbrains.buildServer.serverSide.executors.ExecutorServices;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomAsyncBuildTriggerFactoryImpl extends AsyncBuildTriggerFactoryImpl implements CustomAsyncBuildTriggerFactory {
    @NotNull
    private final ExecutorServices executorServices;
    @NotNull
    private final JobStatusStorageHolder jobStatusStorageHolder;
    @NotNull
    private final ServiceLocator serviceLocator;

    public CustomAsyncBuildTriggerFactoryImpl(@NotNull ExecutorServices executorServices,
                                              @NotNull JobStatusStorageHolder jobStatusStorageHolder,
                                              @NotNull ServiceLocator serviceLocator) {
        super(executorServices, jobStatusStorageHolder);
        this.executorServices = executorServices;
        this.jobStatusStorageHolder = jobStatusStorageHolder;
        this.serviceLocator = serviceLocator;
    }

    @NotNull
    public <TItem> CustomAsyncPolledBuildTrigger<TItem> createBuildTrigger(@NotNull Class<TItem> itemClazz, @NotNull CustomAsyncBuildTrigger<TItem> trigger, @NotNull Logger logger, @Nullable Integer invocationInterval) {
        return new CustomAsyncPolledBuildTrigger<>(executorServices.getLowPriorityExecutorService(),
                logger,
                jobStatusStorageHolder.getStorage(itemClazz),
                invocationInterval,
                trigger,
                serviceLocator);
    }
}
