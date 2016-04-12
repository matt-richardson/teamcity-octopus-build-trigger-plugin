package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.NeedToDeleteAndRecreateTrigger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Test
public class MachineTest {
    public void can_parse_empty_string_to_null_machine() throws NeedToDeleteAndRecreateTrigger {
        Machine sut = Machine.Parse("");
        Assert.assertEquals(sut.getClass(), NullMachine.class);
    }

    public void can_parse_null_string_to_null_machine() throws NeedToDeleteAndRecreateTrigger {
        Machine sut = Machine.Parse((String)null);
        Assert.assertEquals(sut.getClass(), NullMachine.class);
    }

    public void can_parse_valid_string_to_machine() throws NeedToDeleteAndRecreateTrigger {
        Machine sut = Machine.Parse("Machines-91;MachineNinetyOne;env-1,env-2;role-one,role-two");
        Assert.assertEquals(sut.id, "Machines-91");
        Assert.assertEquals(sut.name, "MachineNinetyOne");
    }

    public void to_string_formats_correctly() throws NeedToDeleteAndRecreateTrigger {
        Machine sut = Machine.Parse("Machines-91;MachineNinetyOne;env-1,env-2;role-one,role-two");
        Assert.assertEquals(sut.toString(), "Machines-91;MachineNinetyOne;env-1,env-2;role-one,role-two");
    }

    public void compare_returns_1_when_passed_machine_has_lower_id() throws NeedToDeleteAndRecreateTrigger {
        Machine machine1 = Machine.Parse("Machines-1;MachineOne;env-1,env-2;role-one,role-two");
        Machine machine2 = Machine.Parse("Machines-2;MachineTwo;env-1,env-2;role-one,role-two");
        Assert.assertEquals(machine2.compareTo(machine1), 1);
    }

    public void compare_returns_0_when_passed_machine_has_same_id() throws NeedToDeleteAndRecreateTrigger {
        Machine machine1 = Machine.Parse("Machines-1;MachineOne;env-1,env-2;role-one,role-two");
        Machine machine2 = Machine.Parse("Machines-1;MachineOneAgain;env-1,env-2;role-one,role-two");
        Assert.assertEquals(machine1.compareTo(machine2), 0);
    }

    public void compare_returns_minus_1_when_passed_machine_has_higher_id() throws NeedToDeleteAndRecreateTrigger {
        Machine machine1 = Machine.Parse("Machines-3;MachineOne;env-1,env-2;role-one,role-two");
        Machine machine2 = Machine.Parse("Machines-21;MachineTwo;env-1,env-2;role-one,role-two");
        Assert.assertEquals(machine1.compareTo(machine2), -1);
    }

    public void can_create_from_map() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Id", "Machines-21");
        map.put("Name", "MachineName");
        List<String> environmentIds = new ArrayList<>();
        environmentIds.add("Env-1");
        environmentIds.add("Env-2");
        map.put("EnvironmentIds", environmentIds);
        List<String> roleIds = new ArrayList<>();
        roleIds.add("Role-1");
        map.put("Roles", roleIds);
        Machine sut = Machine.Parse(map);
        Assert.assertEquals(sut.id, "Machines-21");
        Assert.assertEquals(sut.name, "MachineName");
        Assert.assertEquals(sut.environmentIds[0], "Env-1");
        Assert.assertEquals(sut.environmentIds[1], "Env-2");
        Assert.assertEquals(sut.roleIds[0], "Role-1");
    }

    public void equals_returns_false_when_other_object_is_not_a_machine() {
        Machine sut = new Machine("the-machine-id", "the-machine-name", new String[] { "env-id" }, new String[]{ "role-name" });
        Assert.assertFalse(sut.equals(new Machines()));
    }

    public void equals_returns_false_when_other_object_is_null() {
        Machine sut = new Machine("machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" });
        Assert.assertFalse(sut.equals(null));
    }

    public void equals_returns_true_when_both_objects_are_same() {
        Machine sut = new Machine("machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" });
        Machine other = new Machine("machine-1", "MachineOne", new String[] { "env-id" }, new String[]{ "role-name" });
        Assert.assertTrue(sut.equals(other));
    }
}
