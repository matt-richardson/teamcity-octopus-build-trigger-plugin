package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID;
import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.OCTOPUS_URL;

public class FakeBuildTriggerDescriptor implements BuildTriggerDescriptor {
    @NotNull
    @Override
    public String getTriggerName() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getProperties() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(OCTOPUS_PROJECT_ID, "the-project");
        hashMap.put(OCTOPUS_URL, "the-server");
        return hashMap;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public String getSignature() {
        return null;
    }

    @NotNull
    @Override
    public BuildTriggerService getBuildTriggerService() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return null;
    }

    @NotNull
    @Override
    public String getType() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getParameters() {
        return null;
    }
}
