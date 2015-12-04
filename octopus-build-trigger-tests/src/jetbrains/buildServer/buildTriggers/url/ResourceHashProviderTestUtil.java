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

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Victory.Bedrosova
 * Date: 10/1/12
 * Time: 7:42 PM
 */
final class ResourceHashProviderTestUtil {
  @NotNull
  static URL createURL(@NotNull String url) throws MalformedURLException {
    return new URL(url);
  }

  static void write(@NotNull File f, @NotNull String text) throws IOException {
    FileUtils.writeStringToFile(f, text, "UTF-8", true);
  }

  @NotNull
  static Collection<Throwable> getCauses(@NotNull Throwable t) {
    final ArrayList<Throwable> res = new ArrayList<Throwable>();
    while (t != null) {
      res.add(t);
      t = t.getCause();
    }
    return res;
  }
}
