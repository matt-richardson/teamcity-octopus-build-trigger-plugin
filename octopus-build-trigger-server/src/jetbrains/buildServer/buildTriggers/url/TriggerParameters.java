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
import org.jetbrains.annotations.Nullable;

/**
 * @author Victory.Bedrosova
 */
public class TriggerParameters {
  @NotNull private final String myURL;
  @Nullable private final String myUsername;
  @Nullable private final String myPassword;
  @NotNull private final Integer myConnectionTimeout;
  @Nullable private final String myOldHash;

  private TriggerParameters(@NotNull String URL, @Nullable String username, @Nullable String password, @NotNull Integer connectionTimeout, @Nullable String oldHash) {
    myURL = URL;
    myUsername = username;
    myPassword = password;
    myConnectionTimeout = connectionTimeout;
    myOldHash = oldHash;
  }

  @NotNull
  public String getURL() {
    return myURL;
  }

//  @Nullable
//  public String getUsername() {
//    return myUsername;
//  }
//
//  @Nullable
//  public String getPassword() {
//    return myPassword;
//  }

  @NotNull
  public Integer getConnectionTimeout() { return myConnectionTimeout; }

  @NotNull
  public static TriggerParameters create(@NotNull String url) {
    return new TriggerParameters(url, null, null, OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT, null);
  }

  @NotNull
  public static TriggerParameters create(@NotNull String url, @Nullable String username, @Nullable String password, @NotNull Integer connectionTimeout) {
    return new TriggerParameters(url, username, password, connectionTimeout, null);
  }
  @NotNull
  public static TriggerParameters create(@NotNull String url, @Nullable String username, @Nullable String password, @NotNull Integer connectionTimeout, @Nullable String oldHash) {
    return new TriggerParameters(url, username, password, connectionTimeout, oldHash);
  }

  @NotNull
  public static TriggerParameters create(@NotNull String url, @Nullable String username, @Nullable String password) {
    return new TriggerParameters(url, username, password, OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT, null);
  }

  @Nullable
  public String getOldHash() {
    return myOldHash;
  }
}
