package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApiTaskResponseTest {
    public void can_parse_valid_json_when_not_finished_successfully() throws IOException, ParseException {
        String json = ResourceHandler.getResource("api/tasks/ServerTasks-272");
        ApiTaskResponse sut = new ApiTaskResponse(json);

        Assert.assertFalse(sut.finishedSuccessfully);
        Assert.assertTrue(sut.isCompleted);
    }

    public void can_parse_valid_json_when_finished_successfully() throws IOException, ParseException {
        String json = ResourceHandler.getResource("api/tasks/ServerTasks-620");
        ApiTaskResponse sut = new ApiTaskResponse(json);

        Assert.assertTrue(sut.finishedSuccessfully);
        Assert.assertTrue(sut.isCompleted);
    }
}
