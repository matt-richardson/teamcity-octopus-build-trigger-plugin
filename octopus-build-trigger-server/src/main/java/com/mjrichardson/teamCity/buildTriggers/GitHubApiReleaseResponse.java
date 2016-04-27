package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class GitHubApiReleaseResponse {
    public final String tagName;
    public final String htmlUrl;

    public GitHubApiReleaseResponse(String apiResponse) throws ParseException {
        JSONParser parser = new JSONParser();

        Map release = (Map) parser.parse(apiResponse);

        this.tagName = release.get("tag_name").toString();
        this.htmlUrl = release.get("html_url").toString();
    }
}
