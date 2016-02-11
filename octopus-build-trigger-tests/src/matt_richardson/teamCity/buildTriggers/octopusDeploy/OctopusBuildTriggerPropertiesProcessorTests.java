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

import jetbrains.buildServer.serverSide.InvalidProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.OCTOPUS_APIKEY;
import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID;
import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.OCTOPUS_URL;

@Test
public class OctopusBuildTriggerPropertiesProcessorTests {

    public void returns_error_when_url_is_null() {
      OctopusBuildTriggerPropertiesProcessor processor = new OctopusBuildTriggerPropertiesProcessor();
      Map<String,String> properties = new HashMap<>();
      properties.put(OCTOPUS_APIKEY, "api key");
      properties.put(OCTOPUS_PROJECT_ID, "project-id");
      properties.put(OCTOPUS_URL, null);
      Collection<InvalidProperty> result = processor.process(properties);

      Assert.assertEquals(result.size(), 1);
      InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
      Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_URL);
      Assert.assertEquals(invalidProperty.getInvalidReason(), "URL must be specified");
    }

  public void returns_error_when_api_key_is_null() {
    OctopusBuildTriggerPropertiesProcessor processor = new OctopusBuildTriggerPropertiesProcessor();
    Map<String,String> properties = new HashMap<>();
    properties.put(OCTOPUS_APIKEY, null);
    properties.put(OCTOPUS_PROJECT_ID, "project-id");
    properties.put(OCTOPUS_URL, "api key");
    Collection<InvalidProperty> result = processor.process(properties);

    Assert.assertEquals(result.size(), 1);
    InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
    Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_APIKEY);
    Assert.assertEquals(invalidProperty.getInvalidReason(), "API Key must be specified");
  }

}
