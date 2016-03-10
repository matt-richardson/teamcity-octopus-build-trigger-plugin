package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class ApiRootResponseTest {
    public void can_parse_valid_json() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "    \"Deployments\": \"/api/flooble\",\n" +
                "    \"Machines\": \"/api/whatsit\"\n" +
                "  }\n" +
                "}\n";
        ApiRootResponse sut = new ApiRootResponse(json);
        Assert.assertEquals(sut.deploymentsApiLink, "/api/flooble");
        Assert.assertEquals(sut.machinesApiLink, "/api/whatsit");
    }

    public void trims_off_optional_params() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "    \"Deployments\": \"/api/deployments{/id}{?skip,take,projects,environments,taskState}\"\n" +
                "  }\n" +
                "}\n";
        ApiRootResponse sut = new ApiRootResponse(json);
        Assert.assertEquals(sut.deploymentsApiLink, "/api/deployments");
    }

    public void uses_default_values_if_not_found() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "  }\n" +
                "}\n";

        ApiRootResponse sut = new ApiRootResponse(json);
        Assert.assertEquals(sut.deploymentsApiLink, "/api/deployments");
        Assert.assertEquals(sut.machinesApiLink, "/api/machines");
    }
}
