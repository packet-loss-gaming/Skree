/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.block;

import com.google.common.collect.Lists;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.DyeableData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;

import java.util.Collection;

public class DropRegistry {
    public static boolean dropsSelf(BlockType type) {
        return type == BlockTypes.IRON_ORE || type == BlockTypes.GOLD_ORE;
    }

    public static Collection<ItemStack> createDropsFor(Game game, BlockType type) {
        return createDropsFor(game, type, false);
    }

    public static Collection<ItemStack> createDropsFor(Game game, BlockType type, boolean silkTouch) {
        ItemStackBuilder builder = game.getRegistry().getItemBuilder();
        // TODO incomplete logic
        if (silkTouch) {
            if (type == BlockTypes.LIT_REDSTONE_ORE) {
                return Lists.newArrayList(builder.itemType(BlockTypes.REDSTONE_ORE.getHeldItem().get()).build());
            } else {
                return Lists.newArrayList(builder.itemType(type.getHeldItem().get()).build());
            }
        } else {
            if (dropsSelf(type)) {
                return Lists.newArrayList(builder.itemType(type.getHeldItem().get()).build());
            } else if (type == BlockTypes.COAL_ORE) {
                return Lists.newArrayList(builder.itemType(ItemTypes.COAL).build());
            } else if (type == BlockTypes.LAPIS_ORE) {
                DyeableData data = game.getRegistry().getManipulatorRegistry().getBuilder(DyeableData.class).get().create().setValue(
                        DyeColors.BLUE
                );
                return Lists.newArrayList(builder.itemType(ItemTypes.DYE).itemData(data).quantity(
                                                  Probability.getRangedRandom(4, 8)
                                          ).build());
            } else if (MultiTypeRegistry.isRedstoneOre(type)) {
                return Lists.newArrayList(builder.itemType(ItemTypes.REDSTONE).quantity(Probability.getRangedRandom(4, 5)).build());
            } else if (type == BlockTypes.DIAMOND_ORE) {
                return Lists.newArrayList(builder.itemType(ItemTypes.DIAMOND).build());
            } else if (type == BlockTypes.EMERALD_ORE) {
                return Lists.newArrayList(builder.itemType(ItemTypes.EMERALD).build());
            } else if (type == BlockTypes.QUARTZ_ORE) {
                return Lists.newArrayList(builder.itemType(ItemTypes.QUARTZ).build());
            }
        }
        return null;
    }
}
