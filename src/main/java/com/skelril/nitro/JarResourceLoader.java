/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class JarResourceLoader {
  private String baseResourcePathName;

  public JarResourceLoader(String baseResourcePathName) {
    this.baseResourcePathName = baseResourcePathName;
  }

  private FileSystem getFileSystem(URI uri) throws IOException {
    try {
      return FileSystems.getFileSystem(uri);
    } catch (FileSystemNotFoundException e) {
      return FileSystems.newFileSystem(uri, Collections.<String, String>emptyMap());
    }
  }

  public void loadFromResources(Consumer<Function<String, Path>> execute) {
    try {
      URI uri = getClass().getResource(baseResourcePathName).toURI();
      if (uri.getScheme().equals("jar")) {
        try (FileSystem fileSystem = getFileSystem(uri)) {
          Function<String, Path> providerFunction = (resourceName) -> {
            return fileSystem.getPath(baseResourcePathName + resourceName);
          };
          execute.accept(providerFunction);
        }
      } else {
        execute.accept(Paths::get);
      }
    } catch (Exception e) {
      System.err.println("Error loading: " + baseResourcePathName);
      e.printStackTrace();
    }
  }

}
