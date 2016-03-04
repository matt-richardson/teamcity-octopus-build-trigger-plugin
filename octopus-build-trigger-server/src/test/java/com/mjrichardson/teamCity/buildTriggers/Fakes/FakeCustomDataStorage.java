package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.serverSide.CustomDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FakeCustomDataStorage implements CustomDataStorage {
    private String storedDataValue;

    public FakeCustomDataStorage() {
        this(null);
    }

    public FakeCustomDataStorage(String storedDataValue) {
        this.storedDataValue = storedDataValue;
    }

    @Override
    public void putValues(@NotNull Map<String, String> map) {

    }

    @Nullable
    @Override
    public Map<String, String> getValues() {
        return null;
    }

    @Nullable
    @Override
    public String getValue(@NotNull String s) {
        return storedDataValue;
    }

    @Override
    public void putValue(@NotNull String s, @Nullable String s1) {
        storedDataValue = s1;
    }

    @Override
    public void flush() {

    }

    @Override
    public void dispose() {

    }
}
