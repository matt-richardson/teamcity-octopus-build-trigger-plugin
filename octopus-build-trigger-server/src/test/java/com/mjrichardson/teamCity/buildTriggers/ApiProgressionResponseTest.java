package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environment;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

@Test
public class ApiProgressionResponseTest {
    static String ProjectWithNoDeployments = "Projects-23";
    static String ProjectWithLatestDeploymentSuccessful = "Projects-24";
    static String ProjectWithNoSuccessfulDeployments = "Projects-25";
    static String ProjectWithMultipleEnvironments = "Projects-28";
    static String ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful = "Projects-27";
    static String ProjectWithNoRecentSuccessfulDeployments = "Projects-26";
    static String ProjectWithNoReleases = "Projects-101";

    public void can_parse_progression_response_with_no_deployments() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithNoDeployments);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 1);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(environments[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void can_parse_progression_response_with_no_releases() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithNoReleases);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 1);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(environments[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void can_parse_progression_response_with_no_successful_deployments() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithNoSuccessfulDeployments);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertFalse(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 1);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 1, 21, 13, 32, 59, 991));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void can_parse_progression_response_with_latest_deployment_successful() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithLatestDeploymentSuccessful);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 1);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 1, 21, 13, 31, 56, 22));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 13, 31, 56, 22));
    }

    public void can_parse_progression_response_with_multiple_environments() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithMultipleEnvironments);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 2);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 1, 21, 14, 26, 14, 747));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 14, 25, 40, 247));
        Assert.assertEquals(environments[1].environmentId, "Environments-21");
        Assert.assertEquals(environments[1].latestDeployment, new OctopusDate(2016, 1, 21, 14, 25, 53, 700));
        Assert.assertEquals(environments[1].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 14, 25, 53, 700));
    }

    public void can_parse_progression_response_with_multiple_environments_and_most_recent_deployment_successful() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 2);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 1, 21, 14, 24, 30, 935));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 14, 24, 30, 935));
        Assert.assertEquals(environments[1].environmentId, "Environments-21");
        Assert.assertEquals(environments[1].latestDeployment, new OctopusDate(2016, 1, 21, 14, 24, 10, 872));
        Assert.assertEquals(environments[1].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 14, 24, 10, 872));
    }

    public void can_parse_progression_response_with_no_recent_successful_deployments() throws IOException, ParseException, org.json.simple.parser.ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        final String json = ResourceHandler.getResource("api/progression/" + ProjectWithNoRecentSuccessfulDeployments);
        ApiProgressionResponse sut = new ApiProgressionResponse(json);
        Assert.assertTrue(sut.haveCompleteInformation);
        Assert.assertEquals(sut.environments.size(), 1);
        Environment[] environments = sut.environments.toArray();
        Assert.assertEquals(environments[0].environmentId, "Environments-1");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 1, 21, 14, 18, 1, 887));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 1, 21, 13, 35, 27, 179));
    }
}
