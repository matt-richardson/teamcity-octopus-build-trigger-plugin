package com.mjrichardson.teamCity.buildTriggers;

import jetbrains.buildServer.serverSide.ProjectNotFoundException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class ApiDeploymentsResponse {
    public final Deployments deployments;
    public String nextLink;

    public ApiDeploymentsResponse(String deploymentsResponse) throws URISyntaxException, IOException, ParseException, java.text.ParseException, ProjectNotFoundException, com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, UnexpectedResponseCodeException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.deployments = new Deployments();

        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(deploymentsResponse);

        List items = (List) response.get("Items");
        for (Object item : items) {
            deployments.add(Deployment.Parse((Map)item));
        }

        Object nextPage = ((Map) response.get("Links")).get("Page.Next");
        if (nextPage != null)
            nextLink = nextPage.toString();
    }
}
