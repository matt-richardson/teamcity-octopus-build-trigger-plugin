/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class MachinesTest {
    public void can_convert_single_machine_from_string_and_back_again() throws Exception {
        final String expected = new Machine("machine-id", "machine-name").toString();
        Machines machines = Machines.Parse(expected);
        Assert.assertEquals(machines.toString(), expected);
    }

    public void can_convert_multiple_machines_from_string_and_back_again() throws Exception {
        final String expected = String.format("%s|%s",
                new Machine("machine-1", "MachineOne").toString(),
                new Machine("machine-2", "MachineTwo").toString());
        Machines machines = Machines.Parse(expected);
        Assert.assertEquals(machines.toString(), expected);
    }

    public void can_convert_from_empty_string_and_back_again() throws Exception {
        Machines machines = Machines.Parse("");
        Assert.assertEquals(machines.toString(), "");
    }

    public void new_instance_is_empty() throws Exception {
        Machines machines = new Machines();
        Assert.assertEquals(machines.toString(), "");
        Assert.assertTrue(machines.isEmpty());
    }

    public void is_empty_returns_false_when_has_machines() throws Exception {
        final String expected = String.format("%s|%s",
                new Machine("machine-1", "MachineOne").toString(),
                new Machine("machine-2", "MachineTwo").toString());
        Machines machines = Machines.Parse(expected);
        Assert.assertFalse(machines.isEmpty());
    }

    public void add_adds_to_collection() throws Exception {
        final Machine machine = new Machine("machine-1", "machineOne");
        Machines machines = new Machines();
        machines.add(machine);
        Assert.assertEquals(machines.size(), 1);
        Assert.assertEquals(machines.toString(), machine.toString());
    }

    public void get_next_machine_returns_null_machine_if_no_new_machines() throws Exception {
        final Machine oldMachine = new Machine("machine-2", "MachineTwo");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines newMachines = Machines.Parse(String.format("%s|%s", newMachine.toString(), oldMachine.toString()));
        Machines oldMachines = Machines.Parse(String.format("%s|%s", newMachine.toString(), oldMachine.toString()));

        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertEquals(machine.getClass(), NullMachine.class);
    }

    public void get_next_machine_returns_new_machine() throws Exception {
        final Machine oldMachine = new Machine("machine-2", "MachineTwo");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines newMachines = Machines.Parse(String.format("%s|%s", newMachine.toString(), oldMachine.toString()));
        Machines oldMachines = Machines.Parse(oldMachine.toString());

        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertEquals(machine, newMachine);
    }

    public void get_next_machine_returns_next_machine() throws Exception {
        final Machine oldMachine = new Machine("machine-1", "MachineOne");
        final Machine firstNewMachine = new Machine("machine-2", "MachineTwo");
        final Machine secondNewMachine = new Machine("machine-3", "MachineThree");
        Machines newMachines = Machines.Parse(String.format("%s|%s|%s", oldMachine.toString(), firstNewMachine.toString(), secondNewMachine.toString()));
        Machines oldMachines = Machines.Parse(oldMachine.toString());

        Machine machine = newMachines.getNextMachine(oldMachines);
        Assert.assertEquals(machine, firstNewMachine);
    }

    public void to_array_converts_machines_to_array_successfully() {
        final Machine oldMachine = new Machine("machine-2", "MachineTwo");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines machines = Machines.Parse(String.format("%s|%s", oldMachine.toString(), newMachine.toString()));
        Machine[] array = machines.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0], oldMachine);
        Assert.assertEquals(array[1], newMachine);
    }

    public void add_with_single_machine_adds_item() {
        final Machine machine = new Machine("machine-2", "MachineTwo");
        Machines machines = new Machines();
        machines.add(machine);
        Machine[] array = machines.toArray();
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], machine);
    }

    public void add_with_single_machine_does_not_add_duplicate_machine() {
        final Machine machine = new Machine("machine-2", "MachineTwo");
        Machines machines = new Machines();
        machines.add(machine);
        machines.add(machine);
        Machine[] array = machines.toArray();
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], machine);
    }

    public void add_with_single_machine_does_not_add_null_machine() {
        Machines machines = new Machines();
        machines.add(new NullMachine());
        Assert.assertTrue(machines.isEmpty());
    }

    public void add_with_multiple_machines_adds_items() {
        final Machine oldMachine = new Machine("machine-2", "MachineTwo");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines machines = new Machines();
        machines.add(oldMachine);
        machines.add(newMachine);
        Machines sut = new Machines();
        sut.add(machines);
        Machine[] array = sut.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0], oldMachine);
        Assert.assertEquals(array[1], newMachine);
    }

    public void contains_returns_false_if_no_match() {
        final Machine oldMachine = new Machine("machine-1", "MachineOne");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines machines = new Machines();
        machines.add(oldMachine);
        machines.add(newMachine);
        Assert.assertFalse(machines.contains(new Machine("machine-2", "MachineTwo")));
    }

    public void contains_returns_true_if_match() {
        final Machine oldMachine = new Machine("machine-1", "MachineOne");
        final Machine newMachine = new Machine("machine-3", "MachineThree");
        Machines machines = new Machines();
        machines.add(oldMachine);
        machines.add(newMachine);
        Assert.assertTrue(machines.contains(new Machine("machine-3", "MachineThree")));
    }

    public void remove_removes_specified_machines() {
        final Machine machineOne = new Machine("machine-1", "MachineOne");
        final Machine machineTwo = new Machine("machine-2", "MachineTwo");
        final Machine machineThree = new Machine("machine-3", "MachineThree");
        Machines machines = new Machines();
        machines.add(machineOne);
        machines.add(machineTwo);
        machines.add(machineThree);

        Machines newMachines = new Machines();
        newMachines.add(machineOne);
        newMachines.add(machineTwo);

        Machines deleted = machines.removeMachinesNotIn(newMachines);

        Assert.assertEquals(machines.size(), 2);
        Assert.assertTrue(machines.contains(machineOne));
        Assert.assertTrue(machines.contains(machineTwo));

        Assert.assertEquals(deleted.size(), 1);
        Assert.assertTrue(deleted.contains(machineThree));
    }
}
