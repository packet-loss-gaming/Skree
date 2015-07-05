/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.shovel;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.ICustomTool;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Collection;

public interface ICustomShovel extends ICustomTool {
    @Override
    default String __getToolClass() {
        return "shovel";
    }

    @Override
    default Collection<Block> __getEffectiveBlocks() {
        return Lists.newArrayList(
                Blocks.clay,
                Blocks.dirt,
                Blocks.farmland,
                Blocks.grass,
                Blocks.gravel,
                Blocks.mycelium,
                Blocks.sand,
                Blocks.snow,
                Blocks.snow_layer,
                Blocks.soul_sand
        );
    }

    // Modified Native ItemTool methods

    /**
     * Check whether this Item can harvest the given Block
     */
    default boolean canHarvestBlock(Block blockIn) {
        return blockIn == Blocks.snow_layer || blockIn == Blocks.snow;
    }
}
