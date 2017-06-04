/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.world.World;

public interface DropClearService {
  default boolean cleanup(World extent) {
    return cleanup(extent, 10);
  }

  boolean cleanup(World extent, int seconds);

  boolean checkedCleanup(World extent);

  boolean isActiveFor(World extent);

  void forceCleanup(World extent);
}
