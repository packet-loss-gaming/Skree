/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.transformer;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;

public class ForgeTransformer {

    /*
     * Position transformations
     */
    public static Vector3i tf(BlockPos blockPos) {
        return new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos tf(Vector3i blockPos) {
        return new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /*
     * World transformations
     */
    public static World tf(org.spongepowered.api.world.World world) {
        return (World) world;
    }

    public static org.spongepowered.api.world.World tf(World world) {
        return (org.spongepowered.api.world.World) world;
    }

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
