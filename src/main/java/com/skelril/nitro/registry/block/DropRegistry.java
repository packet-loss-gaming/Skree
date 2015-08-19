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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class DropRegistry {
    public static boolean dropsSelf(BlockType type) {
        return type == BlockTypes.IRON_ORE || type == BlockTypes.GOLD_ORE;
    }

    public static Collection<ItemStack> createDropsFor(Game game, BlockType type) {
        return createDropsFor(game, type, false);
    }

    public static Collection<ItemStack> createDropsFor(Game game, BlockType type, boolean silkTouch) {
        // TODO incomplete logic
        if (silkTouch) {
            if (type == BlockTypes.LIT_REDSTONE_ORE) {
                return Lists.newArrayList(newItemStack(BlockTypes.REDSTONE_ORE.getHeldItem().get()));
            } else {
                return Lists.newArrayList(newItemStack(type.getHeldItem().get()));
            }
        } else {
            if (dropsSelf(type)) {
                return Lists.newArrayList(newItemStack(type.getHeldItem().get()));
            } else if (type == BlockTypes.COAL_ORE) {
                return Lists.newArrayList(newItemStack(ItemTypes.COAL));
            } else if (type == BlockTypes.LAPIS_ORE) {
                DyeableData data = game.getRegistry().getManipulatorRegistry().getBuilder(DyeableData.class).get().create();
                data.set(Keys.DYE_COLOR, DyeColors.BLUE);
                return Lists.newArrayList(newItemStack(ItemTypes.DYE, data, Probability.getRangedRandom(4, 8)));
            } else if (MultiTypeRegistry.isRedstoneOre(type)) {
                return Lists.newArrayList(newItemStack(ItemTypes.REDSTONE, Probability.getRangedRandom(4, 5)));
            } else if (type == BlockTypes.DIAMOND_ORE) {
                return Lists.newArrayList(newItemStack(ItemTypes.DIAMOND));
            } else if (type == BlockTypes.EMERALD_ORE) {
                return Lists.newArrayList(newItemStack(ItemTypes.EMERALD));
            } else if (type == BlockTypes.QUARTZ_ORE) {
                return Lists.newArrayList(newItemStack(ItemTypes.QUARTZ));
            }
        }
        return null;
    }
}
