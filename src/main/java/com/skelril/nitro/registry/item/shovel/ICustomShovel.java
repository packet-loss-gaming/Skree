/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.shovel;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.ICustomTool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Collection;

public interface ICustomShovel extends ICustomTool {
    @Override
    default double __getAttackSpeed() {
        return -3.0F; // TODO
    }

    @Override
    default String __getToolClass() {
        return "shovel";
    }

    @Override
    default Collection<Block> __getEffectiveBlocks() {
        return Lists.newArrayList(
                Blocks.CLAY,
                Blocks.DIRT,
                Blocks.FARMLAND,
                Blocks.GRASS,
                Blocks.GRAVEL,
                Blocks.MYCELIUM,
                Blocks.SAND,
                Blocks.SNOW,
                Blocks.SNOW_LAYER,
                Blocks.SOUL_SAND
        );
    }

    // Modified Native ItemTool methods

    /**
     * Check whether this Item can harvest the given Block
     */
    default boolean canHarvestBlock(IBlockState blockIn) {
        Block block = blockIn.getBlock();
        return block == Blocks.SNOW_LAYER || block == Blocks.SNOW;
    }
}
