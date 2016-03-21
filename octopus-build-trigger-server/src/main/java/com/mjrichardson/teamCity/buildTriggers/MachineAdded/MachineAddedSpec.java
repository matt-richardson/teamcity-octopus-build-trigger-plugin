package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MachineAddedSpec {
    @NotNull
    private final String url;
    @NotNull
    final String machineName;
    @Nullable
    final String machineId;
    @NotNull
    final String environmentIds;
    @NotNull
    final String roleIds;

    MachineAddedSpec(@NotNull String url,
                     @NotNull String machineName,
                     @Nullable String machineId,
                     @NotNull String[] environmentIds,
                     @NotNull String[] roleIds) {
        this.url = url;
        this.machineName = machineName;
        this.machineId = machineId;
        this.environmentIds = String.join(",", environmentIds);
        this.roleIds = String.join(",", roleIds);
    }

    public MachineAddedSpec(String octopusUrl, String machineName) {
        this(octopusUrl, machineName, null, new String[0], new String[0]);

    }

    public MachineAddedSpec(String octopusUrl, Machine machine) {
        this(octopusUrl, machine.name, machine.id , machine.environmentIds, machine.roleIds);
    }

    public String getRequestorString() {
        return String.format("Machine %s added to %s", machineName, url);
    }
}
