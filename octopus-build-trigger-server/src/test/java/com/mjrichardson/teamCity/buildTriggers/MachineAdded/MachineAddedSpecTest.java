package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class MachineAddedSpecTest {
//    public void can_create_requestor_string_when_version_not_supplied() {
//        MachineAddedSpec sut = new MachineAddedSpec("theurl", "theproject");
//        Assert.assertEquals(sut.getRequestorString(), "Machine of project theproject created on theurl");
//    }

    public void can_create_requestor_string() {
        MachineAddedSpec sut = new MachineAddedSpec("theurl", "thename");
        Assert.assertEquals(sut.getRequestorString(), "Machine thename added to theurl");
    }

    public void to_string_converts_correctly() {
        String[] environmentIds = new String[2];
        environmentIds[0] = "env-1";
        environmentIds[1] = "env-22";
        String[] roleIds = new String[2];
        roleIds[0] = "role-one";
        roleIds[1] = "role-two";
        Machine machine = new Machine("machine-id", "machine-name", environmentIds, roleIds);
        MachineAddedSpec sut = new MachineAddedSpec("the-url", machine);
        String result = sut.toString();
        Assert.assertEquals(result, "{ url: 'the-url', machineName: 'machine-name', machineId: 'machine-id', environmentIds: 'env-1,env-22', roleIds: 'role-one,role-two' }");
    }
}
