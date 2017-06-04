/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.decorator;

import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Decorators {
  public static final Decorator ZONE_PRIMARY_DECORATOR = new WEDecorator(getBaseWorkingDir());

  private static Path getBaseWorkingDir() {
    ConfigManager service = Sponge.getGame().getConfigManager();
    Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
    try {
      return Files.createDirectories(path.resolve("zones"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
