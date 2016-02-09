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

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import com.intellij.openapi.diagnostic.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

//todo: add tests
public class ApiRootResponse {
  final String projectApiLink;
  final String deploymentsApiLink;
  final String progressionApiLink;

  private static final Logger LOG = Logger.getInstance(OctopusBuildTrigger.class.getName());

  public ApiRootResponse(String apiResponse) throws ParseException {
    projectApiLink = parseLink(apiResponse, "Projects");
    deploymentsApiLink = parseLink(apiResponse, "Deployments");
    //todo: implement fallback to support multiple versions
    progressionApiLink = "/api/progression/"; // parseLink(apiResponse, "Progression"); //todo: fix after bug fixed by ODs
  }

  private String parseLink(String apiResponse, String linkName) throws ParseException {
    LOG.debug("Parsing '" + apiResponse + "' for link '" + linkName + "'");
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(apiResponse);
    final String link = (String)((Map)response.get("Links")).get(linkName);
    final String result = link.replaceAll("\\{.*\\}", ""); //remove all optional params
    LOG.debug("Found link for '" + linkName + "' was '" + result + "'");
    return result;
  }
}
