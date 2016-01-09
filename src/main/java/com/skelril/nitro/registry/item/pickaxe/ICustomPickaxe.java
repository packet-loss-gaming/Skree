/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.pickaxe;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.item.ICustomTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
                Blocks.activator_rail,
                Blocks.coal_ore,
                Blocks.cobblestone,
                Blocks.detector_rail,
                Blocks.diamond_block,
                Blocks.diamond_ore,
                Blocks.double_stone_slab,
                Blocks.golden_rail,
                Blocks.gold_block,
                Blocks.gold_ore,
                Blocks.ice,
                Blocks.iron_block,
                Blocks.iron_ore,
                Blocks.lapis_block,
                Blocks.lapis_ore,
                Blocks.lit_redstone_ore,
                Blocks.mossy_cobblestone,
                Blocks.netherrack,
                Blocks.packed_ice,
                Blocks.rail,
                Blocks.redstone_ore,
                Blocks.sandstone,
                Blocks.red_sandstone,
                Blocks.stone,
                Blocks.stone_slab
        );
    }

    // Modified Native ItemTool methods

    default boolean canHarvestBlock(Block blockIn) {
        if (blockIn == Blocks.obsidian) {
            return __getHarvestTier().getHarvestLevel() == 3;
        } else if (blockIn != Blocks.diamond_block && blockIn != Blocks.diamond_ore) {
            if (blockIn != Blocks.emerald_ore && blockIn != Blocks.emerald_block) {
                if (blockIn != Blocks.gold_block && blockIn != Blocks.gold_ore) {
                    if (blockIn != Blocks.iron_block && blockIn != Blocks.iron_ore) {
                        if (blockIn != Blocks.lapis_block && blockIn != Blocks.lapis_ore) {
                            if (blockIn != Blocks.redstone_ore && blockIn != Blocks.lit_redstone_ore) {
                                return (blockIn.getMaterial() == Material.rock || (blockIn.getMaterial() == Material.iron || blockIn.getMaterial() == Material.anvil));
                            } else {
                                return __getHarvestTier().getHarvestLevel() >= 2;
                            }
                        } else {
                            return __getHarvestTier().getHarvestLevel() >= 1;
                        }
                    } else {
                        return __getHarvestTier().getHarvestLevel() >= 1;
                    }
                } else {
                    return __getHarvestTier().getHarvestLevel() >= 2;
                }
            } else {
                return __getHarvestTier().getHarvestLevel() >= 2;
            }
        } else {
            return __getHarvestTier().getHarvestLevel() >= 2;
        }
    }

    @Override
    default float getStrVsBlock(ItemStack stack, Block p_150893_2_) {
        return p_150893_2_.getMaterial() != Material.iron && p_150893_2_.getMaterial() != Material.anvil && p_150893_2_.getMaterial() != Material.rock ? ICustomTool.super.getStrVsBlock(
                stack,
                p_150893_2_
        ) : __getSpecializedSpeed();
    }
}
