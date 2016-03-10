/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class ApiRootResponse {
    public final String deploymentsApiLink;
    public final String progressionApiLink; //todo: not used outside of tests
    public final String projectsApiLink;
    public final String machinesApiLink;

    private static final Logger LOG = Logger.getInstance(ApiRootResponse.class.getName());

    public ApiRootResponse(String apiResponse) throws ParseException {
        deploymentsApiLink = parseLink(apiResponse, "Deployments", "/api/deployments");
        progressionApiLink = parseLink(apiResponse, "Progression", "/api/progression");
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
