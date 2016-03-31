package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.ServiceNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FakeServiceLocator implements ServiceLocator {
    @NotNull
    @Override
    public <T> T getSingletonService(@NotNull Class<T> aClass) throws ServiceNotFoundException {
        return null;
    }

    @Nullable
    @Override
    public <T> T findSingletonService(@NotNull Class<T> aClass) {
        return null;
    }

    @NotNull
    @Override
    public <T> Collection<T> getServices(@NotNull Class<T> aClass) {
        return null;
    }
}
