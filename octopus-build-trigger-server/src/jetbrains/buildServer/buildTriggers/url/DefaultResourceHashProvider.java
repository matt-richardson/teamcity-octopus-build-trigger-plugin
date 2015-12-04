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

package jetbrains.buildServer.buildTriggers.url;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * User: Victory.Bedrosova
 * Date: 9/28/12
 * Time: 5:39 PM
 */
final class DefaultResourceHashProvider implements ResourceHashProvider {
  @NotNull
  public String getResourceHash(@NotNull TriggerParameters triggerParameters) throws ResourceHashProviderException {
    try {
      return Util.getDigest(triggerParameters);
    } catch (Throwable e) {
      throw new ResourceHashProviderException("URL " + triggerParameters.getURL() + ": " + e.getMessage(), e);
    }
  }
}
