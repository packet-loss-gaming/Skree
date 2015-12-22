/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.data.util.LightLevelUtil;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.PopulatorObject;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.gen.populator.Mushroom;
import org.spongepowered.api.world.gen.type.MushroomType;

import java.util.Optional;
import java.util.Random;

public class WildernessWorldGeneratorModifier implements WorldGeneratorModifier {
    @Override
    public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.getPopulators().add(getMushroomPopulator());
    }

    @Override
    public String getId() {
        return "skree:wilderness";
    }

    @Override
    public String getName() {
        return "Wilderness";
    }

    private Mushroom getMushroomPopulator() {
        return Mushroom.builder().mushroomsPerChunk(3).type(new MushroomType() {
            private PopulatorObject populator = new PopulatorObject() {
                @Override
                public boolean canPlaceAt(World world, int x, int y, int z) {
                    Optional<Integer> lightLevel = LightLevelUtil.getMaxLightLevel(new Location<>(world, x, y, z));
                    if (y <= 1 || y > 40 || !lightLevel.isPresent() || lightLevel.get() < 9) {
                        return false;
                    }
                    return world.getBlockType(x, y, z) == BlockTypes.AIR && world.getBlockType(x, y - 1, z) == BlockTypes.STONE;
                }

                @Override
                public void placeObject(World world, Random random, int x, int y, int z) {
                    world.setBlockType(x, y, z, (BlockType) CustomBlockTypes.MAGIC_MUSHROOM);
                    world.setBlockType(x, y - 1, z, (BlockType) CustomBlockTypes.MAGIC_STONE);
                }

                @Override
                public String getId() {
                    return "skree:magic_mushroom_populator";
                }

                @Override
                public String getName() {
                    return "Magic Mushroom Populator";
                }
            };

            @Override
            public PopulatorObject getPopulatorObject() {
                return populator;
            }

            @Override
            public void setPopulatorObject(PopulatorObject object) {
                this.populator = object;
            }

            @Override
            public String getId() {
                return "skree:magic_mushroom_generator";
            }

            @Override
            public String getName() {
                return "Magic Mushroom Generator";
            }
        }, 1).build();
    }
}
