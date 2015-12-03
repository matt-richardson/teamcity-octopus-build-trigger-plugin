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

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.SimpleFileHttpServer;
import org.apache.http.conn.ConnectTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Victory.Bedrosova
 * Date: 10/2/12
 * Time: 6:01 PM
 */
@Test
public class HttpResourceHashProviderTest extends BaseTestCase {
  SimpleFileHttpServer myServer;
  File myHome;

  @BeforeMethod
  public void setUp() throws Exception {
    myHome = createTempDir();
    myServer = new SimpleFileHttpServer(myHome) {
      @Nullable
      @Override
      protected String getRequestPath(@NotNull String request) {
        final Matcher matcher = Pattern.compile(".*(GET|HEAD)\\s+(\\S+)\\s.*", Pattern.DOTALL).matcher(request);
        if (matcher.matches()) {
          return matcher.group(2);
        }
        return null;
      }
    };
    myServer.start();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    myServer.stop();
  }

  public void testNoServer() throws Exception {
    try {
      getResourceHash("http://unexisting");
    } catch (ResourceHashProviderException e) {
      assertTrue(e.getCause() instanceof UnknownHostException);
      // expected
    }
  }

  public void testNoFile() throws Exception {
    try {
      getResourceHash(getUrl(null, "unexisting"));
    } catch (ResourceHashProviderException e) {
      // expected
    }
  }

  public void testFile() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file, true);

    write(file, "A");
    assertHash(String.valueOf(file.hashCode()), fileUrl); // SimpleFileHttpServer returns file.hashCode() as ETag

    write(file, "BC");
    assertHash(String.valueOf(file.hashCode()), fileUrl);

    write(file, "DEF");
    assertHash(String.valueOf(file.hashCode()), fileUrl);
  }

  public void testAuthFails() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file, false);

    try {
      getResourceHash(fileUrl);
    } catch (ResourceHashProviderException e) {
      // expected
    }
  }

  public void testSeparateCredentials() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file, false);

    write(file, "A");
    assertEquals(
      String.valueOf(file.hashCode()),
      createHttpHashProvider().getResourceHash(TriggerParameters.create(fileUrl, "admin", "admin"))); // SimpleFileHttpServer returns file.hashCode() as ETag
  }

  public void testConnectionTimeout() throws Exception {
    final long before = new Date().getTime();
    try {
      createHttpHashProvider().getResourceHash(TriggerParameters.create("http://www.jetbrains.com:81", "admin", "admin", 10));
    } catch (Throwable e) {
      final Collection<Throwable> causes = ResourceHashProviderTestUtil.getCauses(e);
      for (Throwable t : causes) {
        if (t instanceof ConnectTimeoutException) {
          // expected
          return;
        }
      }
      System.out.println(causes);
      fail();
    }
    assertTrue(new Date().getTime() - before < 1000);
  }

  @NotNull
  private String getResourceHash(@NotNull String url) throws Exception {
    return createHttpHashProvider().getResourceHash(url);
  }

  @NotNull
  private HttpResourceHashProvider createHttpHashProvider() {
    return new HttpResourceHashProvider();
  }

  @NotNull
  private String getUrl(@Nullable String userInfo, @NotNull String path) {
    return "http://" + (userInfo == null ? "" : userInfo + "@") + "localhost:" + myServer.getPort() + "/" + path;
  }

  @NotNull
  private String toUrl(@NotNull File file, boolean withCredentials) throws IOException {
    final String relativePath = getRelativePath(file);
    assertNotNull(relativePath);
    return getUrl(withCredentials ? "admin:admin" : null, relativePath.replace("\\", "/"));
  }

  @Nullable
  private String getRelativePath(@NotNull File file) {
    return FileUtil.getRelativePath(myHome, file);
  }

  private void write(@NotNull File f, @NotNull String text) throws IOException {
    ResourceHashProviderTestUtil.write(f, text);
  }

  private void assertHash(@NotNull String hash, @NotNull String url) throws Exception {
    assertEquals(url, hash, getResourceHash(url));
  }
}
