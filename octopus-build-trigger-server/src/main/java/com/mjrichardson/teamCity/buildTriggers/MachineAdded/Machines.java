package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;

public class Machines {
    private ArrayList<Machine> statusMap;

    public Machines() {
        this("");
    }

    //todo: this ctor should move to a Parse method.
    public Machines(String oldStoredData) {
        this.statusMap = new ArrayList<>();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {
            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    statusMap.add(Machine.Parse(pair));
                }
            }
        }
    }

    public Machines(Machine oldMachine) {
        this(oldMachine.toString());
    }

    @Override
    public String toString() {
        String result = "";
        Collections.sort(statusMap);
        for (Machine Machine : statusMap) {
            result = String.format("%s%s|", result, Machine.toString());
        }
        return result.replaceAll("\\|+$", "");
    }

    public boolean isEmpty() {
        return statusMap.size() == 0;
    }

    public boolean contains(Machine other) {
        for (Machine Machine : statusMap) {
            if (Machine.id.equals(other.id))
                return true;
        }
        return false;
    }

    public void add(Machines Machines) {
        for (Machine Machine : Machines.statusMap) {
            if (!contains(Machine)) {
                add(Machine);
            }
        }
    }

    public void add(Machine Machine) {
        if (Machine.getClass() != NullMachine.class && !contains(Machine))
            statusMap.add(Machine);
    }

    public Machine getNextMachine(Machines oldMachines) {
        Collections.sort(statusMap);

        //for some unknown reason, was getting a concurrent modification exception here when this was a foreach
        for (int i = 0; i < statusMap.size(); i++) {
            Machine machine = statusMap.get(i);
            if (!oldMachines.contains(machine))
                return machine;
        }
        return new NullMachine();
    }

    public int size() {
        return statusMap.size();
    }

    public Machine[] toArray() {
        return statusMap.toArray(new Machine[0]);
    }
}
