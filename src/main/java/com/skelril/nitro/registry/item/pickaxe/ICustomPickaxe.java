/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.pickaxe;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.ICustomTool;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface ICustomPickaxe extends ICustomTool {
  @Override
  default String __getToolClass() {
    return "pickaxe";
  }

  @Override
  default Collection<Block> __getEffectiveBlocks() {
    return Lists.newArrayList(
        Blocks.ACTIVATOR_RAIL,
        Blocks.COAL_ORE,
        Blocks.COBBLESTONE,
        Blocks.DETECTOR_RAIL,
        Blocks.DIAMOND_BLOCK,
        Blocks.DIAMOND_ORE,
        CustomBlockTypes.JURACK_ORE,
        Blocks.DOUBLE_STONE_SLAB,
        Blocks.GOLDEN_RAIL,
        Blocks.GOLD_BLOCK,
        Blocks.GOLD_ORE,
        Blocks.ICE,
        Blocks.IRON_BLOCK,
        Blocks.IRON_ORE,
        Blocks.LAPIS_BLOCK,
        Blocks.LAPIS_ORE,
        Blocks.LIT_REDSTONE_ORE,
        Blocks.MOSSY_COBBLESTONE,
        Blocks.NETHERRACK,
        Blocks.PACKED_ICE,
        Blocks.RAIL,
        Blocks.REDSTONE_ORE,
        Blocks.SANDSTONE,
        Blocks.RED_SANDSTONE,
        Blocks.STONE,
        Blocks.STONE_SLAB
    );
  }

  // Modified Native ItemTool methods

  default boolean canHarvestBlock(IBlockState blockIn) {
    if (blockIn == CustomBlockTypes.JURACK_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 3;
    }
    if (blockIn == Blocks.OBSIDIAN) {
      return __getHarvestTier().getHarvestLevel() >= 3;
    }
    if (blockIn == Blocks.DIAMOND_BLOCK || blockIn == Blocks.DIAMOND_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 2;
    }
    if (blockIn == Blocks.EMERALD_ORE || blockIn == Blocks.EMERALD_BLOCK) {
      return __getHarvestTier().getHarvestLevel() >= 2;
    }
    if (blockIn == Blocks.GOLD_BLOCK || blockIn == Blocks.GOLD_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 2;
    }
    if (blockIn == Blocks.IRON_BLOCK || blockIn == Blocks.IRON_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 1;
    }
    if (blockIn == Blocks.LAPIS_BLOCK || blockIn == Blocks.LAPIS_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 1;
    }
    if (blockIn == Blocks.REDSTONE_ORE || blockIn == Blocks.LIT_REDSTONE_ORE) {
      return __getHarvestTier().getHarvestLevel() >= 2;
    }
    return (blockIn.getMaterial() == Material.ROCK || (blockIn.getMaterial() == Material.IRON || blockIn.getMaterial() == Material.ANVIL));
  }

  @Override
  default double __getAttackSpeed() {
    return -2.8F;
  }

  @Override
  default float getStrVsBlock(ItemStack stack, IBlockState state) {
    return state.getMaterial() != Material.IRON && state.getMaterial() != Material.ANVIL && state.getMaterial() != Material.ROCK ? ICustomTool.super.getStrVsBlock(
        stack,
        state
    ) : __getSpecializedSpeed();
  }
}
