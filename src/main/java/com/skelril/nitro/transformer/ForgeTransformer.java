/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.transformer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.entity.living.player.Player;

public class ForgeTransformer {

    /*
     * ItemStack transformations
     */
    public static ItemStack tf(org.spongepowered.api.item.inventory.ItemStack stack) {
        return (ItemStack) (Object) stack;
    }

    public static org.spongepowered.api.item.inventory.ItemStack tf(ItemStack stack) {
        return (org.spongepowered.api.item.inventory.ItemStack) (Object) stack;
    }

    /*
     * Player transformations
     */
    public static EntityPlayer tf(Player player) {
        return (EntityPlayer) player;
    }

    public static Player tf(EntityPlayer player) {
        return (Player) player;
    }
}
