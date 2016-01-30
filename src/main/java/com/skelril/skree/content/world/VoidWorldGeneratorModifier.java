/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class VoidWorldGeneratorModifier implements WorldGeneratorModifier {
    @Override
    public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings, WorldGenerator worldGenerator) {
        for (BiomeType biomeType : Sponge.getRegistry().getAllOf(BiomeType.class)) {
            worldGenerator.getBiomeSettings(biomeType).getPopulators().clear();
        }

        worldGenerator.setBaseGenerationPopulator(new VoidTerrainGenerator());
    }

    @Override
    public String getId() {
        return "skree:void";
    }

    @Override
    public String getName() {
        return "Void";
    }
}