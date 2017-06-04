/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry;

import com.skelril.nitro.JarResourceLoader;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractCustomRegistrySystem {
  private JarResourceLoader jarResourceLoader;

  public AbstractCustomRegistrySystem(String baseResourcePathName) {
    this.jarResourceLoader = new JarResourceLoader(baseResourcePathName);
  }

  protected void loadFromResources(Consumer<Function<String, Path>> execute) {
    jarResourceLoader.loadFromResources(execute);
  }

  public void preInit() {
  }

  public void associate() {
  }

  public void init() {
  }
}
