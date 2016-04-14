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

    public ApiRootResponse(String apiResponse, AnalyticsTracker analyticsTracker) throws ParseException {
        JSONParser parser = new JSONParser();
        LOG.debug("Parsing '" + apiResponse + "'");
        Map response = (Map) parser.parse(apiResponse);

        deploymentsApiLink = parseLink(response, "Deployments", "/api/deployments");
        projectsApiLink = parseLink(response, "Projects", "/api/projects");
        machinesApiLink = parseLink(response, "Machines", "/api/machines");
        analyticsTracker.setOctopusVersion((String)response.get("Version"));
        analyticsTracker.setOctopusApiVersion((String)response.get("ApiVersion"));
    }

    private String parseLink(Map response, String linkName, String defaultResponse) throws ParseException {
        LOG.debug("Extracting link '" + linkName + "'");
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
