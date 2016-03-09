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
