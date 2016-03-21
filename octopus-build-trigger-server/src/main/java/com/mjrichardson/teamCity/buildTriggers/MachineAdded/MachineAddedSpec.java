package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MachineAddedSpec {
    @NotNull
    private final String url;
    @Nullable
    final String name; //todo: rename to machineName

//    MachineAddedSpec(@NotNull String url) {
//        this(url, null);
//    }

    MachineAddedSpec(@NotNull String url, @Nullable String name) {
        this.url = url;
        this.name = name;
    }

    public String getRequestorString() {
//        if (name == null)
//            return String.format("Machine %s added to %s", name, url);
        return String.format("Machine %s added to %s", name, url);
    }
}
