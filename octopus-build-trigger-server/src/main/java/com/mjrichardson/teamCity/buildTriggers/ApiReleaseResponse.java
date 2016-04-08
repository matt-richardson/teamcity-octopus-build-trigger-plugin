package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class ApiReleaseResponse {
    public String releaseId;
    public String version;

    public ApiReleaseResponse(String releaseResponse) throws ParseException {
        JSONParser parser = new JSONParser();

        Map release = (Map) parser.parse(releaseResponse);

        releaseId = release.get("Id").toString();
        version = release.get("Version").toString();
    }
}
