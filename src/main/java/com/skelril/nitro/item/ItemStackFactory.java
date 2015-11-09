/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;

public class ItemStackFactory {

    private static Game game;

    public ItemStackFactory(Game game) {
        ItemStackFactory.game = game;
    }

    public static ItemStack newItemStack(ItemType type) {
        return newItemStack(type, 1);
    }

    public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data) {
        return newItemStack(type, data, 1);
    }

    public static ItemStack newItemStack(ItemType type, int quantity) {
        return game.getRegistry().createBuilder(ItemStackBuilder.class).itemType(type).quantity(quantity).build();
    }

    public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data, int quantity) {
        return game.getRegistry().createBuilder(ItemStackBuilder.class).itemType(type).quantity(quantity).itemData(data).build();
    }

    public static ItemStack newItemStack(ItemStack itemStack) {
        return game.getRegistry().createBuilder(ItemStackBuilder.class).fromItemStack(itemStack).build();
    }

    public static ItemStack newItemStack(ItemStack itemStack, int quantity) {
        return game.getRegistry().createBuilder(ItemStackBuilder.class).fromItemStack(itemStack).quantity(quantity).build();
    }
}
