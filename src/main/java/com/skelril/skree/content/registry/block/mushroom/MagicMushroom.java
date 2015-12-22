/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.mushroom;

import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Random;

public class MagicMushroom extends BlockBush implements IGrowable, ICustomBlock {

    public MagicMushroom() {
        float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(25) == 0) {
            Iterator iterator = BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)).iterator();

            for (int i = 0; iterator.hasNext();) {
                BlockPos blockpos1 = (BlockPos)iterator.next();

                if (worldIn.getBlockState(blockpos1).getBlock() == this) {
                    --i;

                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockpos2 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int j = 0; j < 4; ++j)  {
                if (worldIn.isAirBlock(blockpos2) && this.canBlockStay(worldIn, blockpos2, this.getDefaultState())) {
                    pos = blockpos2;
                }

                blockpos2 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (worldIn.isAirBlock(blockpos2) && this.canBlockStay(worldIn, blockpos2, this.getDefaultState())) {
                worldIn.setBlockState(blockpos2, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }


    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return ground.isFullBlock();
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
            return worldIn.getLight(pos) < 9 && iblockstate1.getBlock() == CustomBlockTypes.MAGIC_STONE;
        } else {
            return false;
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    }

    @Override
    public String __getID() {
        return "magic_mushroom";
    }
}
