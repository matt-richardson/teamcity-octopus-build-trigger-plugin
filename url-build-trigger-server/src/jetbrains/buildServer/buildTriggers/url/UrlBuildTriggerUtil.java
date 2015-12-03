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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: vbedrosova
 * Date: 06.12.10
 * Time: 13:38
 */
public final class UrlBuildTriggerUtil {
  public static String URL_PARAM = "url.build.trigger.url";
  public static String USERNAME_PARAM = "url.build.trigger.username";
  public static String PASSWORD_PARAM = "url.build.trigger.password";

  public static String POLL_INTERVAL_PROP = "url.build.trigger.poll.interval";
  public static final Integer DEFAULT_POLL_INTERVAL = 30; // seconds

  public static final String CONNECTION_TIMEOUT_PROP = "url.build.trigger.connection.timeout";
  public static final Integer DEFAULT_CONNECTION_TIMEOUT = 60 * 1000; // milliseconds

  public static final String ENABLED_PROTOCOLS_PROP = "url.build.trigger.enabled.protocols";
  public static final String DEFAULT_ENABLED_PROTOCOLS = "ftp,http,ftps,https";

  static final String UNEXITING_RESOURCE_HASH = "";

  static String getResourceHash(long lastModified, long size) {
    return lastModified + "#" + size;
  }

  @NotNull
  static String getDigest(@NotNull final TriggerParameters parameters) throws IOException {
    final URLConnection con = new URL(parameters.getURL()).openConnection();
    con.setConnectTimeout(parameters.getConnectionTimeout());
    con.setReadTimeout(2 * parameters.getConnectionTimeout());
    return getDigest(con.getInputStream());
  }

  @NotNull
  static String getDigest(@NotNull final InputStream source) throws IOException {
    try {
      final MessageDigest digest = MessageDigest.getInstance("MD5");
      final DigestInputStream dis = new DigestInputStream(source, digest);
      final byte[] bytes = new byte[32768];

      while (dis.read(bytes) > 0) {
        // just read
      }

      return toHex(digest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("MD5 not installed", e);
    } finally {
      source.close();
    }
  }

  private static String toHex(byte[] arg) throws IOException {
    return String.format("%x", new BigInteger(arg));
  }
}
