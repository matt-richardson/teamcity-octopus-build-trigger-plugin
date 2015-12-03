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

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Victory.Bedrosova
 * Date: 9/28/12
 * Time: 7:53 PM
 */
final class ResourceHashProviderFactory {

  @NotNull
  public static ResourceHashProvider createResourceHashProvider(@NotNull String url) {
    if (isProtocolEnabled(url)) {
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return new HttpResourceHashProvider();
      }
      if (url.startsWith("ftp://") || url.startsWith("ftps://")) {
        return new FtpResourceHashProvider();
      }
      if (url.startsWith("file://")) {
        return new FileResourceHashProvider();
      }
      return new DefaultResourceHashProvider();
    }
    if (StringUtil.hasParameterReferences(url)) {
      throw new BuildTriggerException("Url contains unresolved parameter references");
    }
    throw new BuildTriggerException(getProtocolDisabledError(url));
  }

  @Nullable
  public static String checkUrl(@NotNull String url) {
    if (isProtocolEnabled(url)) {
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return null;
      }
      if (url.startsWith("ftp://") || url.startsWith("ftps://")) {
        return null;
      }
      if (url.startsWith("file://")) {
        return null;
      }
      return null;
    }
    if (StringUtil.hasParameterReferences(url)) {
      return null;
    }
    return getProtocolDisabledError(url);
  }

  private static boolean isProtocolEnabled(@NotNull String urlStr) {
    for (String p : getEnabledProtocols().split(",")) {
      if (urlStr.startsWith(p.trim() + "://")) return true;
    }
    return false;
  }

  @NotNull
  private static String getEnabledProtocols() {
    return TeamCityProperties.getProperty(UrlBuildTriggerUtil.ENABLED_PROTOCOLS_PROP, UrlBuildTriggerUtil.DEFAULT_ENABLED_PROTOCOLS).trim();
  }

  @NotNull
  private static String getProtocolDisabledError(@NotNull String urlStr) {
    return urlStr + ": protocol disabled, currently enabled protocols: " + getEnabledProtocols() +
           ". To enable protocol edit " + UrlBuildTriggerUtil.ENABLED_PROTOCOLS_PROP + " internal property";
  }
}
