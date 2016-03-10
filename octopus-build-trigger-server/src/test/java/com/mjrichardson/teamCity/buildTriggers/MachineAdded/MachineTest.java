package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class MachineTest {
    public void can_parse_empty_string_to_null_machine() {
        Machine sut = Machine.Parse("");
        Assert.assertEquals(sut.getClass(), NullMachine.class);
    }

    public void can_parse_null_string_to_null_machine() {
        Machine sut = Machine.Parse((String)null);
        Assert.assertEquals(sut.getClass(), NullMachine.class);
    }

    public void can_parse_valid_string_to_machine() {
        Machine sut = Machine.Parse("Machines-91;MachineNintyOne");
        Assert.assertEquals(sut.id, "Machines-91");
        Assert.assertEquals(sut.name, "MachineNintyOne");
    }

    public void to_string_formats_correctly() {
        Machine sut = Machine.Parse("Machines-91;MachineNintyOne");
        Assert.assertEquals(sut.toString(), "Machines-91;MachineNintyOne");
    }

    public void compare_returns_1_when_passed_machine_has_lower_id() {
        Machine machine1 = Machine.Parse("Machines-1;MachineOne");
        Machine machine2 = Machine.Parse("Machines-2;MachineTwo");
        Assert.assertEquals(machine2.compareTo(machine1), 1);
    }

    public void compare_returns_0_when_passed_machine_has_same_id() {
        Machine machine1 = Machine.Parse("Machines-1;MachineOne");
        Machine machine2 = Machine.Parse("Machines-1;MachineOneAgain");
        Assert.assertEquals(machine1.compareTo(machine2), 0);
    }

    public void compare_returns_minus_1_when_passed_machine_has_higher_id() {
        Machine machine1 = Machine.Parse("Machines-3;MachineOne");
        Machine machine2 = Machine.Parse("Machines-21;MachineTwo");
        Assert.assertEquals(machine1.compareTo(machine2), -1);
    }

    public void can_create_from_map() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Id", "Machines-21");
        map.put("Name", "MachineName");
        Machine sut = Machine.Parse(map);
        Assert.assertEquals(sut.id, "Machines-21");
        Assert.assertEquals(sut.name, "MachineName");
    }

    public void equals_returns_false_when_other_object_is_not_a_machine() {
        Machine sut = new Machine("machine-1", "MachineOne");
        Assert.assertFalse(sut.equals(new Machines()));
    }

    public void equals_returns_false_when_other_object_is_null() {
        Machine sut = new Machine("machine-1", "MachineOne");
        Assert.assertFalse(sut.equals(null));
    }

    public void equals_returns_true_when_both_objects_are_same() {
        Machine sut = new Machine("machine-1", "MachineOne");
        Machine other = new Machine("machine-1", "MachineOne");
        Assert.assertTrue(sut.equals(other));
    }
}
