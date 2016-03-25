package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

@Test
public class ApiRootResponseTest {
    public void can_parse_valid_json() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "    \"Deployments\": \"/api/flooble\",\n" +
                "    \"Machines\": \"/api/whatsit\"\n" +
                "  }\n" +
                "}\n";
        ApiRootResponse sut = new ApiRootResponse(json, new FakeAnalyticsTracker());
        Assert.assertEquals(sut.deploymentsApiLink, "/api/flooble");
        Assert.assertEquals(sut.machinesApiLink, "/api/whatsit");
    }

    public void trims_off_optional_params() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "    \"Deployments\": \"/api/deployments{/id}{?skip,take,projects,environments,taskState}\"\n" +
                "  }\n" +
                "}\n";
        ApiRootResponse sut = new ApiRootResponse(json, new FakeAnalyticsTracker());
        Assert.assertEquals(sut.deploymentsApiLink, "/api/deployments");
    }

    public void uses_default_values_if_not_found() throws ParseException {
        final String json = "{\n" +
                "  \"Links\": {\n" +
                "  }\n" +
                "}\n";

        ApiRootResponse sut = new ApiRootResponse(json, new FakeAnalyticsTracker());
        Assert.assertEquals(sut.deploymentsApiLink, "/api/deployments");
        Assert.assertEquals(sut.machinesApiLink, "/api/machines");
    }

    public void sets_version_numbers_on_analytics_tracker() throws IOException, ParseException {
        InputStream resource = getClass().getResourceAsStream("/responses/3.3.0/api.json");
        String json = IOUtils.toString(resource);

        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        new ApiRootResponse(json, analyticsTracker);
        Assert.assertEquals(analyticsTracker.octopusVersion, "3.3.0-beta0002");
        Assert.assertEquals(analyticsTracker.octopusApiVersion, "3.0.0");
    }
}
