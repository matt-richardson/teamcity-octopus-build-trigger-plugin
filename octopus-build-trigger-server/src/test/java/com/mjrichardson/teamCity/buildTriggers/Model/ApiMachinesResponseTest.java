package com.mjrichardson.teamCity.buildTriggers.Model;

import com.mjrichardson.teamCity.buildTriggers.MachineAdded.Machine;
import com.mjrichardson.teamCity.buildTriggers.Model.ApiMachinesResponse;
import com.mjrichardson.teamCity.buildTriggers.ResourceHandler;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApiMachinesResponseTest {
    public void can_parse_machines_response() throws IOException, ParseException {
        final String json = ResourceHandler.getResource("api/machines");
        ApiMachinesResponse sut = new ApiMachinesResponse(json);

        Assert.assertFalse(sut.machines.isEmpty());
        Assert.assertEquals(sut.machines.size(), 1);
        Machine[] array = sut.machines.toArray();
        Assert.assertEquals(array[0].id, "Machines-1");
        Assert.assertEquals(array[0].name, "Octopus Server");
        Assert.assertEquals(sut.nextLink, null);
    }

    public void can_parse_machines_response_with_no_machines() throws IOException, ParseException {
        final String json = ResourceHandler.getResource("api/machines-with-no-items");
        ApiMachinesResponse sut = new ApiMachinesResponse(json);

        Assert.assertEquals(sut.machines.size(), 0);
        Assert.assertTrue(sut.machines.isEmpty());
    }
}
