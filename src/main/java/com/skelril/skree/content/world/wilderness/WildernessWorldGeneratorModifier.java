/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

public class WildernessWorldGeneratorModifier implements WorldGeneratorModifier {

  @Override
  public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
    worldGenerator.getPopulators().add(new MagicMushroomPopulator(1));
    worldGenerator.getPopulators().add(new JurackOrePopulator());
  }

  @Override
  public String getId() {
    return "skree:wilderness";
  }

  @Override
  public String getName() {
    return "Wilderness";
  }
}
