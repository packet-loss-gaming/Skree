/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.api.world.Chunk;
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
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        Vector3i min = chunk.getBlockMin();
        Vector3i max = chunk.getBlockMax();

        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int z = min.getZ(); z <= max.getZ(); ++z) {
                if (random.nextInt(20) != 0) {
                    continue;
                }

                for (int y = min.getY(); y < 20; ++y) {
                    BlockPos searchPoint = new BlockPos(x, y, z);
                    if (world.getBlockState(searchPoint).getBlock() == Blocks.lava) {
                        BlockPos above = searchPoint.add(0, 1, 0);
                        if (world.getBlockState(above).getBlock() == Blocks.lava) {
                            BlockPos lowPoint = searchPoint.add(0, -1, 0);
                            if (world.getBlockState(lowPoint).getBlock() == Blocks.stone) {
                                world.setBlockState(lowPoint, CustomBlockTypes.JURACK_ORE.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }
}
