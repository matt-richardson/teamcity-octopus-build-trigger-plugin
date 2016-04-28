package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class ApiDeploymentProcessResponse {
    public String version;

    public ApiDeploymentProcessResponse(String deploymentProcessResponse) throws ParseException {
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(deploymentProcessResponse);

        this.version = response.get("Version").toString();
    }
}
