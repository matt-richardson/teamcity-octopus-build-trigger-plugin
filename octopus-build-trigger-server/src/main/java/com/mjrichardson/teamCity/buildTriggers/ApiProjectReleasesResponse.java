package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Release;
import com.mjrichardson.teamCity.buildTriggers.ReleaseCreated.Releases;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;

public class ApiProjectReleasesResponse {
    public Releases releases;
    public String nextLink;

    public ApiProjectReleasesResponse(String releasesResponse) throws ParseException {
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(releasesResponse);

        releases = new Releases();

        List items = (List) response.get("Items");
        for (Object item : items) {
            releases.add(Release.Parse((Map) item));
        }

        Object nextPage = ((Map) response.get("Links")).get("Page.Next");
        if (nextPage != null)
            nextLink = nextPage.toString();
    }
}
