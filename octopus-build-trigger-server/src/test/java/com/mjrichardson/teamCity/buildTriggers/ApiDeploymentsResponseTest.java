package com.mjrichardson.teamCity.buildTriggers;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class ApiDeploymentsResponseTest {
    public void can_parse_valid_response_without_next_link() throws ParseException, IOException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, java.text.ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        String json = ResourceHandler.getResource("api/deployments/Projects=Projects-25");
        ApiDeploymentsResponse sut = new ApiDeploymentsResponse(json);
        Assert.assertEquals(sut.nextLink, null);
        Assert.assertEquals(sut.deployments.size(), 1);
        Deployment[] deployments = sut.deployments.toArray();
        Assert.assertEquals(deployments[0].deploymentId, "Deployments-82");
        Assert.assertEquals(deployments[0].environmentId, "Environments-1");
        Assert.assertEquals(deployments[0].createdDate, new OctopusDate(2016,1,21,13,32,59,991));
        Assert.assertEquals(deployments[0].taskLink, "/api/tasks/ServerTasks-272");
    }

    public void can_parse_valid_response_with_next_link() throws IOException, ParseException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, java.text.ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        String json = ResourceHandler.getResource("api/deployments/Projects=Projects-121");
        ApiDeploymentsResponse sut = new ApiDeploymentsResponse(json);
        Assert.assertEquals(sut.nextLink, "/api/deployments?skip=30&projects=Projects-121");
        Assert.assertEquals(sut.deployments.size(), 30);
    }
}
