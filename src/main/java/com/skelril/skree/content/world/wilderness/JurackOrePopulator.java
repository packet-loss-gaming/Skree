/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorType;
import org.spongepowered.api.world.gen.PopulatorTypes;

import java.util.Random;

public class JurackOrePopulator implements Populator {
    @Override
    public PopulatorType getType() {
        return PopulatorTypes.ORE;
    }

    @Override
    public void populate(World world, Extent volume, Random random) {
        Vector3i min = volume.getBlockMin();
        Vector3i max = volume.getBlockMax();

        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int z = min.getZ(); z <= max.getZ(); ++z) {
                if (random.nextInt(20) != 0) {
                    continue;
                }

                for (int y = min.getY(); y < 20; ++y) {
                    Vector3i searchPoint = new Vector3i(x, y, z);
                    if (world.getBlockType(searchPoint) == BlockTypes.LAVA) {
                        Vector3i above = searchPoint.add(0, 1, 0);
                        if (world.getBlockType(above) == BlockTypes.LAVA) {
                            Vector3i lowPoint = searchPoint.add(0, -1, 0);
                            if (world.getBlockType(lowPoint) == BlockTypes.STONE) {
                                world.setBlockType(lowPoint, (BlockType) CustomBlockTypes.JURACK_ORE, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
