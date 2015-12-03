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
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * User: vbedrosova
 * Date: 07.12.10
 * Time: 13:49
 */
@Test
public class FileResourceHashProviderTest extends BaseTestCase {
  public void testNoFile() throws Exception {
    assertUnexisting("file:///path/to/file");
  }

  public void testFile() throws Exception {
    final File file = createTempFile();
    final String fileUrl = toUrl(file);

    write(file, "A");
    assertHash(file.lastModified(), 1, fileUrl);

    write(file, "BC");
    assertHash(file.lastModified(), 3, fileUrl);

    write(file, "DEF");
    assertHash(file.lastModified(), 6, fileUrl);
  }

  public void testFolderWithFiles() throws Exception {
    final File dir = createTempDir();
    final String dirUrl = toUrl(dir);

    {
      final File file1 = new File(dir, "file1");

      write(file1, "A");
      assertHash(dir.lastModified(), 1, dirUrl);

      write(file1, "BC");
      assertHash(dir.lastModified(), 3, dirUrl);

      write(file1, "DEF");
      assertHash(dir.lastModified(), 6, dirUrl);
    }

    {
      final File file2 = new File(dir, "file2");

      write(file2, "G");
      assertHash(dir.lastModified(), 7, dirUrl);

      write(file2, "HI");
      assertHash(dir.lastModified(), 9, dirUrl);

      write(file2, "JKL");
      assertHash(dir.lastModified(), 12, dirUrl);
    }

    {
      final File file3 = new File(dir, "dir/file3");

      write(file3, "M");
      assertHash(dir.lastModified(), 13, dirUrl);

      write(file3, "NO");
      assertHash(dir.lastModified(), 15, dirUrl);

      write(file3, "PQR");
      assertHash(dir.lastModified(), 18, dirUrl);
    }
  }

  @NotNull
  private FileResourceHashProvider createFileHashProvider() {
    return new FileResourceHashProvider();
  }

  @NotNull
  private String getResourceHash(@NotNull String url) throws Exception {
    return createFileHashProvider().getResourceHash(url);
  }

  private void assertUnexisting(@NotNull String url) throws Exception {
    final String hash = getResourceHash(url);
    assertEquals(hash, UrlBuildTriggerUtil.UNEXITING_RESOURCE_HASH, hash);
  }

  @NotNull
  private String toUrl(@NotNull File file) throws IOException {
    return FileUtils.toURLs(new File[]{file})[0].toExternalForm();
  }

  private void write(@NotNull File f, @NotNull String text) throws IOException {
    ResourceHashProviderTestUtil.write(f, text);
  }

  private void assertHash(long lastModified, long size, @NotNull String url) throws Exception {
    assertEquals(url, lastModified + "#" + size, getResourceHash(url));
  }
}
