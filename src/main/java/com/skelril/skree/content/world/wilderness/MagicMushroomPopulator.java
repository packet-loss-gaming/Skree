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

public class MagicMushroomPopulator implements Populator {

    private final int mushroomCount;

    public MagicMushroomPopulator(int mushroomCount) {
        this.mushroomCount = mushroomCount;
    }

    @Override
    public PopulatorType getType() {
        return PopulatorTypes.MUSHROOM;
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        Vector3i min = chunk.getBlockMin();
        BlockPos chunkPos = new BlockPos(min.getX(), min.getY(), min.getZ());

        for (int i = 0; i < mushroomCount; ++i) {
            int x = random.nextInt(16) + 8;
            int z = random.nextInt(16) + 8;
            int y = random.nextInt(40);
            generate(world, random, chunkPos.add(x, y, z));
        }
    }

    private boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos1 = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            BlockPos blockpos2 = blockpos1.add(0, -1, 0);

            if (worldIn.getBlockState(blockpos2).getBlock() == Blocks.stone && worldIn.isAirBlock(blockpos1) && (!worldIn.provider.getHasNoSky() || blockpos1.getY() < 40) && CustomBlockTypes.MAGIC_MUSHROOM.canBlockStayGen(worldIn, blockpos1, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState()))  {
                worldIn.setBlockState(blockpos2, CustomBlockTypes.MAGIC_STONE.getDefaultState(), 2);
                worldIn.setBlockState(blockpos1, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState(), 2);
            }
        }

        return true;
    }
}
