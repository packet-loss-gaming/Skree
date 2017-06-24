/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorType;
import org.spongepowered.api.world.gen.PopulatorTypes;

import java.util.Random;

public class MagicMushroomPopulator implements Populator {
  @Override
  public PopulatorType getType() {
    return PopulatorTypes.MUSHROOM;
  }

  @Override
  public void populate(World world, Extent volume, Random random) {
    Vector3i min = volume.getBlockMin();
    Vector3i size = volume.getBlockSize();
    BlockPos chunkPos = new BlockPos(min.getX(), min.getY(), min.getZ());

    for (int i = 0; i < 64; ++i) {
      int x = random.nextInt(size.getX());
      int z = random.nextInt(size.getZ());
      int y = random.nextInt(40);

      BlockPos targetBlock = chunkPos.add(x, y, z);
      BlockPos targetBaseBlock = targetBlock.add(0, -1, 0);

      generate((net.minecraft.world.World) world, random, targetBlock, targetBaseBlock);
    }
  }

  private boolean generate(net.minecraft.world.World worldIn, Random rand, BlockPos targetBlock, BlockPos targetBaseBlock) {
    boolean baseIsStone = worldIn.getBlockState(targetBaseBlock).getBlock() == BlockTypes.STONE;
    if (baseIsStone && worldIn.isAirBlock(targetBlock) && (!worldIn.provider.hasNoSky() || targetBlock.getY() < 40) && CustomBlockTypes.MAGIC_MUSHROOM.canBlockStayGen(worldIn, targetBlock, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState())) {
      worldIn.setBlockState(targetBaseBlock, CustomBlockTypes.MAGIC_STONE.getDefaultState(), 2);
      worldIn.setBlockState(targetBlock, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState(), 2);
    }

    return true;
  }
}
