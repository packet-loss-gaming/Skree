/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.axe;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.CustomTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public abstract class CustomAxe extends CustomTool {

    @Override
    public String __getToolClass() {
        return "axe";
    }

    @Override
    public Collection<Block> __getEffectiveBlocks() {
        return Lists.newArrayList(
                Blocks.planks,
                Blocks.bookshelf,
                Blocks.log,
                Blocks.log2,
                Blocks.chest,
                Blocks.pumpkin,
                Blocks.lit_pumpkin,
                Blocks.melon_block,
                Blocks.ladder
        );
    }

    // Modified Native ItemTool methods

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        return block.getMaterial() != Material.wood && block.getMaterial() != Material.plants && block.getMaterial() != Material.vine ? super.getStrVsBlock(stack, block) : __getSpecializedSpeed();
    }
}
