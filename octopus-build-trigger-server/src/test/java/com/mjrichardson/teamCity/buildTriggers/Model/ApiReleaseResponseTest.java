package com.mjrichardson.teamCity.buildTriggers.Model;

import com.mjrichardson.teamCity.buildTriggers.Model.ApiReleaseResponse;
import com.mjrichardson.teamCity.buildTriggers.ResourceHandler;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApiReleaseResponseTest {
    public void can_parse_machines_response() throws IOException, ParseException {
        final String json = ResourceHandler.getResource("api/releases/Releases-219");
        ApiReleaseResponse sut = new ApiReleaseResponse(json);

        Assert.assertEquals(sut.releaseId, "Releases-219");
        Assert.assertEquals(sut.version, "0.0.6");
    }
}
