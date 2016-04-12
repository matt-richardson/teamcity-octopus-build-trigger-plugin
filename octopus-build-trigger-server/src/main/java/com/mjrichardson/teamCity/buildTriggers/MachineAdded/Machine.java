package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Machine implements Comparable<Machine> {
    @NotNull
    public final String id;
    @NotNull
    public final String name;
    @NotNull
    public final String[] environmentIds;
    @NotNull
    public final String[] roleIds;

    public Machine(String id, String name) {
        this(id, name, new String[0], new String[0]);
    }

    public Machine(@NotNull String id, @NotNull String name, @NotNull String[] environmentIds, @NotNull String[] roleIds) {
        this.id = id;
        this.name = name;
        this.environmentIds = environmentIds;
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return id + ";" + name;
    }

    public static Machine Parse(Map item) {
        String id = item.get("Id").toString();
        String name = item.get("Name").toString();

        List items = (List) item.get("EnvironmentIds");
        ArrayList<String> environmentIds = new ArrayList<>();
        for (Object environmentId : items) {
            environmentIds.add((String)environmentId);
        }

        items = (List) item.get("Roles");
        ArrayList<String> roleIds = new ArrayList<>();
        for (Object roleId : items) {
            roleIds.add((String)roleId);
        }

        return new Machine(id, name, environmentIds.toArray(new String[0]), roleIds.toArray(new String[0]));
    }

    public static Machine Parse(String pair) {
        if (pair == null || pair == "") {
            return new NullMachine();
        }
        final Integer DONT_REMOVE_EMPTY_VALUES = -1;
        final String[] split = pair.split(";", DONT_REMOVE_EMPTY_VALUES);
        final String id = split[0];
        final String name = split[1];

        Machine result = new Machine(id, name);
        if (result.equals(new NullMachine()))
            return new NullMachine();
        return result;
    }

    @Override
    public int compareTo(Machine o) {
        Integer thisId = Integer.parseInt(id.split("-")[1]);
        Integer otherId = Integer.parseInt(o.id.split("-")[1]);
        return thisId.compareTo(otherId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != Machine.class && obj.getClass() != NullMachine.class)
            return false;
        return toString().equals(obj.toString());
    }
}
