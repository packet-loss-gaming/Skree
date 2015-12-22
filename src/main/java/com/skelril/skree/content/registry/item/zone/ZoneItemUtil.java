/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class ZoneItemUtil {
    public static Optional<String> getZone(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getZone((ItemStack) (Object) stack);
    }

    public static Optional<String> getZone(ItemStack stack) {
        if (isZoneItem(stack) && hasZoneData(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
            return Optional.of(tag.getString("zone"));
        }
        return Optional.empty();
    }

    public static boolean isZoneItem(org.spongepowered.api.item.inventory.ItemStack stack) {
        return isZoneSlaveItem(stack) || isZoneMasterItem(stack);
    }

    public static boolean isZoneItem(ItemStack stack) {
        return isZoneItem((org.spongepowered.api.item.inventory.ItemStack) (Object) stack);
    }

    public static boolean isZoneMasterItem(org.spongepowered.api.item.inventory.ItemStack stack) {
        if (stack != null) {
            if (CustomItemTypes.ZONE_MASTER_ORB.equals(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isZoneMasterItem(ItemStack stack) {
        return isZoneMasterItem((org.spongepowered.api.item.inventory.ItemStack) (Object) stack);
    }

    public static boolean isZoneSlaveItem(org.spongepowered.api.item.inventory.ItemStack stack) {
        if (stack != null) {
            if (CustomItemTypes.ZONE_SLAVE_ORB.equals(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isZoneSlaveItem(ItemStack stack) {
        return isZoneSlaveItem((org.spongepowered.api.item.inventory.ItemStack) (Object) stack);
    }

    public static boolean isAttuned(org.spongepowered.api.item.inventory.ItemStack stack) {
        return isAttuned((ItemStack) (Object) stack);
    }

    public static boolean isAttuned(ItemStack stack) {
        return isZoneItem(stack) && stack.getMetadata() > 0;
    }

    public static void attune(org.spongepowered.api.item.inventory.ItemStack stack) {
        attune((ItemStack) (Object) stack);
    }

    public static void attune(ItemStack stack) {
        if (isZoneItem(stack)) {
            stack.setItemDamage(1);
        }
    }

    public static Optional<Player> getGroupOwner(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getGroupOwner((ItemStack) (Object) stack);
    }

    public static Optional<Player> getGroupOwner(ItemStack stack) {
        if (isZoneSlaveItem(stack) && hasZoneData(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
            UUID ownerID = UUID.fromString(tag.getString("owner"));
            return SkreePlugin.inst().getGame().getServer().getPlayer(ownerID);
        }
        throw new IllegalArgumentException("Invalid ItemStack provided");
    }

    public static String getGroupOwnerName(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getGroupOwnerName((ItemStack) (Object) stack);
    }

    public static String getGroupOwnerName(ItemStack stack) {
        if (isZoneSlaveItem(stack) && hasZoneData(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
            return tag.getString("owner-name");
        }
        throw new IllegalArgumentException("Invalid ItemStack provided");
    }

    public static void setMasterToZone(org.spongepowered.api.item.inventory.ItemStack stack, String zone) {
        setMasterToZone((ItemStack) (Object) stack, zone);
    }

    public static void setMasterToZone(ItemStack stack, String zone) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_zone_data")) {
            stack.getTagCompound().setTag("skree_zone_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
        tag.setString("zone", zone);

        attune(stack);
    }

    private static boolean hasZoneData(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("skree_zone_data");
    }

    public static org.spongepowered.api.item.inventory.ItemStack createForMaster(org.spongepowered.api.item.inventory.ItemStack inStack, Player zoneOwner) {
        if (!isAttuned(inStack)) {
            throw new IllegalArgumentException("Can't create a slave from an unattuned master stack.");
        }

        ItemStack stack = new ItemStack(CustomItemTypes.ZONE_SLAVE_ORB);
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_zone_data")) {
            stack.getTagCompound().setTag("skree_zone_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
        tag.setString("zone", getZone(inStack).get());
        tag.setString("owner-name", zoneOwner.getName());
        tag.setString("owner", zoneOwner.getUniqueId().toString());
        return (org.spongepowered.api.item.inventory.ItemStack) (Object) stack;
    }

    public static org.spongepowered.api.item.inventory.ItemStack createForMaster(ItemStack stack, Player player) {
        return createForMaster((org.spongepowered.api.item.inventory.ItemStack) (Object) stack, player);
    }

}
