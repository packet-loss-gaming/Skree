/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.axe;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.ICustomTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface ICustomAxe extends ICustomTool {

    // Skelril Methods

    @Override
    default String __getToolClass() {
        return "axe";
    }

    @Override
    default double __getAttackSpeed() {
        return -3.0F; // TODO
    }

    @Override
    default Collection<Block> __getEffectiveBlocks() {
        return Lists.newArrayList(
                Blocks.PLANKS,
                Blocks.BOOKSHELF,
                Blocks.LOG,
                Blocks.LOG2,
                Blocks.CHEST,
                Blocks.PUMPKIN,
                Blocks.LIT_PUMPKIN,
                Blocks.MELON_BLOCK,
                Blocks.LADDER
        );
    }

    @Override
    default float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (state.getMaterial() != Material.WOOD && state.getMaterial() != Material.PLANTS && state.getMaterial() != Material.VINE) {
            return ICustomTool.super.getStrVsBlock(stack, state);
        } else {
            return __getSpecializedSpeed();
        }
    }
}
