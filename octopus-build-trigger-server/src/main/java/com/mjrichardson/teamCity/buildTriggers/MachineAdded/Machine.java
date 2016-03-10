package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import java.util.Map;

public class Machine implements Comparable<Machine> {
    public final String id;
    public final String name;

    public Machine(String MachineId, String version) {
        this.id = MachineId;
        this.name = version;
    }

    @Override
    public String toString() {
        return id + ";" + name;
    }

    public static Machine Parse(Map item) {
        String id = item.get("Id").toString();
        String name = item.get("Name").toString();

        return new Machine(id, name);
    }

    public static Machine Parse(String pair) {
        if (pair == null || pair == "") {
            return new NullMachine();
        }
        final Integer DONT_REMOVE_EMPTY_VALUES = -1;
        final String[] split = pair.split(";", DONT_REMOVE_EMPTY_VALUES);
        final String MachineId = split[0];
        final String version = split[1];

        Machine result = new Machine(MachineId, version);
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
