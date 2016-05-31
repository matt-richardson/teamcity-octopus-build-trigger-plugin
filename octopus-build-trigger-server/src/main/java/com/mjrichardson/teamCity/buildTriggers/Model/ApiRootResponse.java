package com.mjrichardson.teamCity.buildTriggers.Model;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.UUID;

public class ApiRootResponse {
    public final String deploymentsApiLink;
    public final String projectsApiLink;
    public final String machinesApiLink;

    private static final Logger LOG = Logger.getInstance(ApiRootResponse.class.getName());

    public ApiRootResponse(String apiResponse, AnalyticsTracker analyticsTracker, UUID correlationId) throws ParseException {
        JSONParser parser = new JSONParser();
        LOG.debug(String.format("%s: Parsing '%s'", correlationId, apiResponse));
        Map response = (Map) parser.parse(apiResponse);

        deploymentsApiLink = parseLink(response, "Deployments", "/api/deployments", correlationId);
        projectsApiLink = parseLink(response, "Projects", "/api/projects", correlationId);
        machinesApiLink = parseLink(response, "Machines", "/api/machines", correlationId);
        analyticsTracker.setOctopusVersion((String)response.get("Version"));
        analyticsTracker.setOctopusApiVersion((String)response.get("ApiVersion"));
    }

    private String parseLink(Map response, String linkName, String defaultResponse, UUID correlationId) throws ParseException {
        LOG.debug(String.format("%s: Extracting link '%s'", correlationId, linkName));
        final String link = (String) ((Map) response.get("Links")).get(linkName);
        if (link == null) {
            LOG.debug(String.format("%s: Didn't find a link in response for '%s'. Using default '%s'", correlationId, linkName, defaultResponse));
            return defaultResponse;
        }
        final String result = link.replaceAll("\\{.*\\}", ""); //remove all optional params
        LOG.debug(String.format("%s: Found link for '%s' was '%s'", correlationId, linkName, result));
        return result;
    }
}
