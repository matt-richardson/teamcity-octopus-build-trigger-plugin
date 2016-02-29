package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.AsyncTriggerParameters;
import jetbrains.buildServer.serverSide.*;
import org.jetbrains.annotations.NotNull;

public class FakeAsyncTriggerParameters implements AsyncTriggerParameters {
    @NotNull
    @Override
    public SBuildType getBuildType() {
        return new FakeSBuildType();
    }

    @NotNull
    @Override
    public BuildTriggerDescriptor getTriggerDescriptor() {
        return new FakeBuildTriggerDescriptor();
    }

    @NotNull
    @Override
    public CustomDataStorage getCustomDataStorage() {
        return new FakeCustomDataStorage();
    }

}
