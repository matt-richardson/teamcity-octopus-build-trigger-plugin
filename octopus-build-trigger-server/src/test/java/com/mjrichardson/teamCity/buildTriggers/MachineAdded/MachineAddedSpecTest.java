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
}
