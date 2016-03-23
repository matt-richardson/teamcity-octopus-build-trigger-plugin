package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class ApiRootResponse {
    public final String deploymentsApiLink;
    public final String projectsApiLink;
    public final String machinesApiLink;

    private static final Logger LOG = Logger.getInstance(ApiRootResponse.class.getName());

    //todo: cache for an hour - this will rarely change

    public ApiRootResponse(String apiResponse) throws ParseException {
        deploymentsApiLink = parseLink(apiResponse, "Deployments", "/api/deployments");
        projectsApiLink = parseLink(apiResponse, "Projects", "/api/projects");
        machinesApiLink = parseLink(apiResponse, "Machines", "/api/machines");
    }

    private String parseLink(String apiResponse, String linkName, String defaultResponse) throws ParseException {
        LOG.debug("Parsing '" + apiResponse + "' for link '" + linkName + "'");
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(apiResponse);
        final String link = (String) ((Map) response.get("Links")).get(linkName);
        if (link == null) {
            LOG.debug("Didn't find a link in response for '" + linkName + "'. Using default '" + defaultResponse + "'");
            return defaultResponse;
        }
        final String result = link.replaceAll("\\{.*\\}", ""); //remove all optional params
        LOG.debug("Found link for '" + linkName + "' was '" + result + "'");
        return result;
    }
}
