package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.NeedToDeleteAndRecreateTrigger;
import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;

public class Machines {
    private ArrayList<Machine> statusMap;

    public Machines() {
        this.statusMap = new ArrayList<>();
    }

    public static Machines Parse(String oldStoredData) throws NeedToDeleteAndRecreateTrigger {
        Machines result = new Machines();

        if (!StringUtil.isEmptyOrSpaces(oldStoredData)) {
            for (String pair : oldStoredData.split("\\|")) {
                if (pair.length() > 0) {
                    result.add(Machine.Parse(pair));
                }
            }
        }
        return result;
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
        for (int i = 0; i < statusMap.size(); i++) {
            Machine machine = statusMap.get(i);
            if (machine.id.equals(other.id))
                return true;
        }
        return false;
    }

    public void add(Machines machines) {
        for (Machine machine : machines.statusMap) {
            if (!contains(machine)) {
                add(machine);
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

    public Machines removeMachinesNotIn(Machines newMachines) {
        Machines deletedMachines = new Machines();

        for (int i = 0; i < statusMap.size(); i++) {
            Machine machine = statusMap.get(i);
            if (!newMachines.contains(machine))
                deletedMachines.add(machine);
        }

        for (int i = 0; i < deletedMachines.size(); i++) {
            Machine machine = deletedMachines.statusMap.get(i);
            statusMap.remove(machine);
        }
        return deletedMachines;
    }
}
