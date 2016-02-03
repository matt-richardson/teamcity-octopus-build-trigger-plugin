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

import java.util.List;
import java.util.Map;

public class ApiProjectResponse {
  final String projectId;
  //todo: log to own file, rather than server log
  private static final Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;

  public ApiProjectResponse(String projectResponse, String octopusProject) throws Exception {
    projectId = getProjectId(projectResponse, octopusProject);
  }

  //todo: figure out if we need this. is the ui going to pass us an id or a name?
  private String getProjectId(String projectResponse, String projectName) throws Exception {
    LOG.debug("Parsing '" + projectResponse + " to find project with name '" + projectName + "'");
    JSONParser parser = new JSONParser();
    Map response = (Map)parser.parse(projectResponse);
    List items = (List)response.get("Items");

    for (Object item: items) {
      Map map = (Map)item;
      if (map.get("Name").toString().equals(projectName)) {
        LOG.debug("Found that project id '" + map.get("Id").toString() + " maps to name '" + projectName + "'");
        return map.get("Id").toString();
      }
      if (map.get("Id").toString().equals(projectName)) {
        LOG.debug("Found that project id '" + map.get("Id").toString() + " equals supplied name '" + projectName + "'");
        return map.get("Id").toString();
      }
    };
    throw new ProjectNotFoundException("Unable to find project '" + projectName + "'");
  }
}
