package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environment;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Environments;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.UnexpectedResponseCodeException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//todo: this does more than just load the response... refactor.
public class ApiProgressionResponse {
    private static final Logger LOG = Logger.getInstance(ApiProgressionResponse.class.getName());
    public Environments environments;
    public Boolean haveCompleteInformation;

    public ApiProgressionResponse(String progressionResponse, UUID correlationId) throws java.text.ParseException, ParseException, UnexpectedResponseCodeException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, IOException {
        LOG.debug(String.format("%s: parsing progression response", correlationId));
        this.haveCompleteInformation = Parse(progressionResponse, correlationId);
    }

    private boolean Parse(String progressionResponse, UUID correlationId) throws java.text.ParseException, ParseException {
        JSONParser parser = new JSONParser();
        Map response = (Map) parser.parse(progressionResponse);

        environments = new Environments();
        List environments = (List) response.get("Environments");
        for (Object environment : environments) {
            Map environmentMap = (Map) environment;
            this.environments.addEnvironment(environmentMap.get("Id").toString());
        }

        List releasesAndDeployments = (List) response.get("Releases");

        if (releasesAndDeployments.size() == 0) {
            LOG.debug(String.format("%s: No releases found in progression api response", correlationId));
            return true;
        }

        Boolean foundDeployment = AddDeployments(releasesAndDeployments);
        if (!foundDeployment) {
            LOG.debug(String.format("%s: No deployments found in progression api response", correlationId));
            return true;
        }

        if (this.environments.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment()) {
            LOG.debug(String.format("%s: All deployments have finished successfully - no need to parse deployment response", correlationId));
            return true;
        }

        return false;
    }

    private Boolean AddDeployments(List releasesAndDeployments) throws java.text.ParseException {
        Boolean foundDeployment = false;

        for (Object releaseAndDeploymentPair : releasesAndDeployments) {
            Map releaseAndDeploymentPairMap = (Map) releaseAndDeploymentPair;
            Map deps = (Map) releaseAndDeploymentPairMap.get("Deployments");
            for (Object key : deps.keySet()) {
                foundDeployment = true;
                Environment environment = Environment.Parse((Map) deps.get(key));
                environments.addOrUpdate(environment);
            }
        }
        return foundDeployment;
    }
}
