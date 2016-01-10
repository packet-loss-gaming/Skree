/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public interface Teleporter {

    default void setDestination(org.spongepowered.api.item.inventory.ItemStack stack, Location<World> target) {
        setDestination(tf(stack), target);
    }

    default void setDestination(ItemStack stack, Location<World> target) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_dest_data")) {
            stack.getTagCompound().setTag("skree_dest_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        tag.setString("world", target.getExtent().getName());
        tag.setDouble("x", target.getX());
        tag.setDouble("y", target.getY());
        tag.setDouble("z", target.getZ());

        stack.setItemDamage(1);
    }

    default Optional<Location<World>> getDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getDestination(tf(stack));
    }

    default Optional<Location<World>> getDestination(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_dest_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        String worldName = tag.getString("world");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");
        Optional<World> optWorld = Sponge.getServer().getWorld(worldName);
        if (optWorld.isPresent()) {
            return Optional.of(new Location<>(optWorld.get(), x, y, z));
        }
        return Optional.empty();
    }

    default Optional<String> getClientDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getClientDestination(tf(stack));
    }

    default Optional<String> getClientDestination(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_dest_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        String worldName = tag.getString("world");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");

        return Optional.of(worldName + " at " + (int) x + ", " + (int) y + ", " + (int) z);
    }
}
