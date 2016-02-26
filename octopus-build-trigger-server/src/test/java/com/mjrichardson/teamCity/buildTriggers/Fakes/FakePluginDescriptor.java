package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class FakePluginDescriptor implements PluginDescriptor {
    @NotNull
    @Override
    public String getPluginName() {
        return null;
    }

    @NotNull
    @Override
    public String getPluginResourcesPath() {
        return null;
    }

    @NotNull
    @Override
    public String getPluginResourcesPath(@NotNull String s) {
        return "resources-path/" + s;
    }

    @Nullable
    @Override
    public String getParameterValue(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public String getPluginVersion() {
        return null;
    }

    @NotNull
    @Override
    public File getPluginRoot() {
        return null;
    }
}
