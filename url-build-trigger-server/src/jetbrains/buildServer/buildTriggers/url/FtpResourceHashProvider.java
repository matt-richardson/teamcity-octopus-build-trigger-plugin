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

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.StringUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

/**
 * User: Victory.Bedrosova
 * Date: 10/1/12
 * Time: 2:35 PM
 */
final public class FtpResourceHashProvider implements ResourceHashProvider {
  private static final Logger LOG = Logger.getLogger(Loggers.VCS_CATEGORY + UrlBuildTrigger.class);

  @NotNull
  public String getResourceHash(@NotNull TriggerParameters triggerParameters) throws ResourceHashProviderException {
    try {
      return getResourceHash(triggerParameters.getURL(), triggerParameters.getUsername(), triggerParameters.getPassword(), triggerParameters.getConnectionTimeout());
    } catch (Throwable e) {
      throw new ResourceHashProviderException("URL " + triggerParameters.getURL() + ": " + e.getMessage(), e);
    }
  }

  @NotNull
  String getResourceHash(@NotNull String url, @Nullable String user, @Nullable String password) throws IOException, ResourceHashProviderException {
    return getResourceHash(url, user, password, UrlBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT);
  }

  @NotNull
  String getResourceHash(@NotNull String urlStr, @Nullable String user, @Nullable String password, @NotNull Integer connectionTimeout) throws IOException, ResourceHashProviderException {
    boolean secure = false;

    if (urlStr.startsWith("ftps://")) {
      urlStr = urlStr.replace("ftps://", "ftp://");
      secure = true;
    }

    final FTPClient ftp = secure ? new FTPSClient() : new FTPClient();

    ftp.setConnectTimeout(connectionTimeout);
    ftp.setDataTimeout(2 * connectionTimeout);

    try {
      final URL url = new URL(urlStr);
      connectAndLogin(ftp, url, user, password);

      final FTPFile file = ftp.mlistFile(url.getFile());
      // currently raw listing returns smth like ize=1;Modify=20121002101740.067;Type=file; file
      // ize=1 seems to stand instead of Size=1
      // Modify seems to have some incorrect time zone
      return file == null ? UrlBuildTriggerUtil.UNEXITING_RESOURCE_HASH : file.getRawListing();

    } finally {
      logoutAndDisconnect(ftp);
    }
  }

  private void connectAndLogin(@NotNull FTPClient ftp, @NotNull URL url, @Nullable String user, @Nullable String password) throws ResourceHashProviderException, IOException {
    connect(ftp, url);
    login(ftp, url, user, password);
  }

  private void connect(@NotNull FTPClient ftp, @NotNull URL url) throws ResourceHashProviderException, IOException {
    final String server = url.getHost();
    final int port = url.getPort();
    int reply;

    if (port > 0) {
      ftp.connect(server, port);
    } else {
      ftp.connect(server);
    }

    reply = ftp.getReplyCode();

    if (!FTPReply.isPositiveCompletion(reply)) {
      throw new ResourceHashProviderException("FTP server " + server + " refused connection with " + reply + " code");
    }

    LOG.debug("Connected to FTP server " + server + " on " + (port > 0 ? port : ftp.getDefaultPort()));
  }

  private void login(@NotNull FTPClient ftp, @NotNull URL url, @Nullable String user, @Nullable String password) throws ResourceHashProviderException, IOException {
    final String host = url.getHost();

    String rUser;
    String rPassword;
    if (StringUtil.isNotEmpty(user)) {
      rUser = user;
      rPassword = password == null ? StringUtil.EMPTY : password;
    } else {
      final String userInfo = url.getUserInfo();
      rUser = getUser(userInfo);
      rPassword = getPassword(userInfo);
    }


    if (!ftp.login(rUser, rPassword)) {
      throw new ResourceHashProviderException("FTP server " + host + " login failed for username " + rUser);
    }

    LOG.debug("Logged in to FTP server " + host + " for username " + rUser);
  }

  private void logoutAndDisconnect(@NotNull FTPClient ftp) {
    if (ftp.isConnected()) {
      try {
        ftp.logout();
        ftp.disconnect();
      } catch (IOException e) {
        LOG.warn(e.getMessage(), e);
      }
    }
  }

  @NotNull
  private String getUser(@Nullable String userInfo) throws ResourceHashProviderException {
    final String user = getUserInfoPart(userInfo, 0);
    return StringUtil.isNotEmpty(user) ? user : "anonymous";
  }

  @NotNull
  private String getPassword(@Nullable String userInfo) throws ResourceHashProviderException {
    return getUserInfoPart(userInfo, 1);
  }

  @NotNull
  private String getUserInfoPart(@Nullable String userInfo, int index) throws ResourceHashProviderException {
    if (userInfo == null) return "";

    final String[] parts = userInfo.split(":");
    return parts.length > index ? parts[index] : StringUtil.EMPTY;
  }
}
