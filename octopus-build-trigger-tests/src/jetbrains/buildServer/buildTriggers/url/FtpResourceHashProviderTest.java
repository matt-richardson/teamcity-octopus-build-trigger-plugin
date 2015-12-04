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
import jetbrains.buildServer.util.FileUtil;
import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * User: Victory.Bedrosova
 * Date: 10/1/12
 * Time: 7:14 PM
 */
@Test
public class FtpResourceHashProviderTest extends BaseTestCase {

  private FtpServer myServer;
  File myHome;

  @BeforeMethod
  public void setUp() throws Exception {
    myHome = createTempDir();

    final FtpServerFactory serverFactory = new FtpServerFactory();

    final ListenerFactory listenerFactory = new ListenerFactory();
    listenerFactory.setPort(8021); // 21 port can't be used under arbitrary user in Unix
    serverFactory.addListener("default", listenerFactory.createListener());

    final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
    final UserManager userManager = userManagerFactory.createUserManager();

    {
      final BaseUser admin = new BaseUser();
      admin.setName("admin");
      admin.setPassword("admin");
      admin.setHomeDirectory(myHome.getAbsolutePath());
      userManager.save(admin);
    }

    {
      final BaseUser anonymous = new BaseUser();
      anonymous.setName("anonymous");
      anonymous.setHomeDirectory(myHome.getAbsolutePath());
      userManager.save(anonymous);
    }

    serverFactory.setUserManager(userManager);

//    SslConfigurationFactory ssl = new SslConfigurationFactory();
//    ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
//    ssl.setKeystorePassword("password");

    myServer = serverFactory.createServer();
    myServer.start();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    myServer.stop();
  }

  public void testNoServer() throws Exception {
    try {
      getResourceHash("ftp://unexisting");
    } catch (UnknownHostException e) {
      // expected
    }
  }

  public void testNoFileAdmin() throws Exception {
    assertUnexisting(getUrl("admin:admin", "unexisting"));
  }

  public void testNoFile() throws Exception {
    assertUnexisting(getUrl(null, "unexisting"));
  }

  public void testNoFileAnonymous() throws Exception {
    assertUnexisting(getUrl("anonymous", "unexisting"));
  }

  public void testNoFileAnonymousWithPassword() throws Exception {
    assertUnexisting(getUrl("anonymous:random", "unexisting"));
  }

  public void testAuthFailure() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file);

    try {
      getResourceHash(getUrl("bad:admin", fileUrl));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testSeparateCredentials() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file);

    getResourceHash(fileUrl, "admin", "admin");
  }

  public void testSeparateCredentialsAuthFailure() throws Exception {
      final File file = new File(myHome, "file");
      final String fileUrl = toUrl(file);

      try {
        getResourceHash(fileUrl, "bad", "admin");
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  public void testFile() throws Exception {
    final File file = new File(myHome, "file");
    final String fileUrl = toUrl(file);

    write(file, "A");
    assertHash(file.lastModified(), 1, fileUrl);

    write(file, "BC");
    assertHash(file.lastModified(), 3, fileUrl);

    write(file, "DEF");
    assertHash(file.lastModified(), 6, fileUrl);
  }

  // currently folders are not supported correctly
  @Test(enabled = false)
  public void testFolderWithFiles() throws Exception {
    final File dir = FileUtil.createDir(new File(myHome, "dir"));
    final String dirUrl = toUrl(dir);

    {
      final File file1 = new File(dir, "file1");

      write(file1, "A");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file1, "BC");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file1, "DEF");
      assertHash(dir.lastModified(), 0, dirUrl);
    }

    {
      final File file2 = new File(dir, "file2");

      write(file2, "G");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file2, "HI");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file2, "JKL");
      assertHash(dir.lastModified(), 0, dirUrl);
    }

    {
      final File file3 = new File(dir, "dir/file3");

      write(file3, "M");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file3, "NO");
      assertHash(dir.lastModified(), 0, dirUrl);

      write(file3, "PQR");
      assertHash(dir.lastModified(), 0, dirUrl);
    }
  }

  public void testConnectionTimeout() throws Exception {
    final long before = new Date().getTime();
    try {
      createFtpHashProvider().getResourceHash(TriggerParameters.create("ftp://www.google.com:81", "admin", "admin", 10));
    } catch (Throwable e) {
      final Collection<Throwable> causes = ResourceHashProviderTestUtil.getCauses(e);
      for (Throwable t : causes) {
        if (t instanceof SocketTimeoutException) {
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
  private String getUrl(@Nullable String userInfo, @NotNull String path) {
    return "ftp://" + (userInfo == null ? "" : userInfo + "@") + "localhost:8021/" + path;
  }

  @NotNull
  private FtpResourceHashProvider createFtpHashProvider() {
    return new FtpResourceHashProvider();
  }

  @NotNull
  private String getResourceHash(@NotNull String url) throws Exception {
    return getResourceHash(url, null, null);
  }

  @NotNull
  private String getResourceHash(@NotNull String url, @Nullable String user, @Nullable String password) throws Exception {
    return createFtpHashProvider().getResourceHash(url, user, password);
  }

  private void assertUnexisting(@NotNull String url) throws Exception {
    assertEquals(OctopusBuildTriggerUtil.UNEXITING_RESOURCE_HASH, getResourceHash(url));
  }

  private void write(@NotNull File f, @NotNull String text) throws IOException {
    ResourceHashProviderTestUtil.write(f, text);
  }

  private void assertHash(long lastModified, long size, @NotNull String url) throws Exception {
    final String resourceHash = getResourceHash(url); //ize=1;Modify=20121002101740.067;Type=file; file

    assertContains(resourceHash, "ize=" + size); // ize instead of size due to bug in apache commons API
    assertContains(resourceHash, "Modify=" + formatTime(lastModified));
  }

  @NotNull
  private String formatTime(long time) {
    return new SimpleDateFormat("yyyyMMdd").format(new Date(time));
  }

  @NotNull
  private String toUrl(@NotNull File file) throws IOException {
    final String relativePath = FileUtil.getRelativePath(myHome, file);
    assertNotNull(relativePath);
    return getUrl("admin:admin", relativePath.replace("\\", "/"));
  }
}
