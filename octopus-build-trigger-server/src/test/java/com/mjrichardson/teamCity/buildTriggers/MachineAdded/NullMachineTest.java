package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.NeedToDeleteAndRecreateTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullMachineTest {
    public void null_machine_sets_fields_to_empty() {
        Machine sut = new NullMachine();
        Assert.assertEquals(sut.id, "");
        Assert.assertEquals(sut.name, "");
    }

    public void can_parse_to_null_machine() throws NeedToDeleteAndRecreateTriggerException {
        Machine sut = Machine.Parse(new NullMachine().toString());
        Assert.assertEquals(sut.getClass(), NullMachine.class);
    }
}
