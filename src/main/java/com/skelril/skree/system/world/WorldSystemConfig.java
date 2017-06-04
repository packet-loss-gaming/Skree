/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;

import java.util.List;

public class WorldSystemConfig {
  private List<ArchetypeConfig> archetypes;
  private List<WorldConfig> worlds;

  public List<ArchetypeConfig> getArchetypes() {
    return archetypes;
  }

  public List<WorldConfig> getWorlds() {
    return worlds;
  }
}
