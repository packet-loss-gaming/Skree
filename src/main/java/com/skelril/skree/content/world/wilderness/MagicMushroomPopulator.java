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

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

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
  public void populate(World world, Extent volume, Random random) {
    Vector3i min = volume.getBlockMin();
    Vector3i chunkPos = new Vector3i(min.getX(), min.getY(), min.getZ());

    for (int i = 0; i < mushroomCount; ++i) {
      int x = random.nextInt(16) + 8;
      int z = random.nextInt(16) + 8;
      int y = random.nextInt(40);
      generate(tf(world), random, tf(chunkPos.add(x, y, z)));
    }
  }

  private boolean generate(net.minecraft.world.World worldIn, Random rand, BlockPos position) {
    for (int i = 0; i < 64; ++i) {
      BlockPos blockpos1 = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
      BlockPos blockpos2 = blockpos1.add(0, -1, 0);

      if (worldIn.getBlockState(blockpos2).getBlock() == BlockTypes.STONE && worldIn.isAirBlock(blockpos1) && (!worldIn.provider.getHasNoSky() || blockpos1.getY() < 40) && CustomBlockTypes.MAGIC_MUSHROOM.canBlockStayGen(worldIn, blockpos1, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState())) {
        worldIn.setBlockState(blockpos2, CustomBlockTypes.MAGIC_STONE.getDefaultState(), 2);
        worldIn.setBlockState(blockpos1, CustomBlockTypes.MAGIC_MUSHROOM.getDefaultState(), 2);
      }
    }

    return true;
  }
}
