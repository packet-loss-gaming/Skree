/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.service.ZoneService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class ZoneItemUtil {

  public static void purgeZoneItems(Player player, @Nullable ItemStack activating) {
    purgeZoneItems(tf(player), activating);
  }

  public static void purgeZoneItems(EntityPlayer player, @Nullable ItemStack activating) {
    NonNullList<ItemStack> itemStacks = player.inventory.mainInventory;
    for (int i = 0; i < itemStacks.size(); ++i) {
      if (isZoneItem(itemStacks.get(i))) {
        if (isZoneMasterItem(itemStacks.get(i))) {
          // Check to see if this is the activating item, if so remove it
          if (activating != null && hasSameZoneID(itemStacks.get(i), activating)) {
            itemStacks.set(i, ItemStack.EMPTY);
            continue;
          }

          // Close groups for master orbs carried by other players
          if (isAttuned(itemStacks.get(i))) {
            rescindGroupInvite(itemStacks.get(i));
            ItemStack reset = new ItemStack(CustomItemTypes.ZONE_MASTER_ORB);
            setMasterToZone(reset, getZone(itemStacks.get(i)).get());
            itemStacks.set(i, reset);
          }

          continue;
        } else if (isZoneSlaveItem(itemStacks.get(i))) {
          if (activating == null || !hasSameZoneID(itemStacks.get(i), activating)) {
            notifyGroupOwner(itemStacks.get(i), (Player) player, false);
          }
        }
        itemStacks.set(i, ItemStack.EMPTY);
      }
    }
    player.inventoryContainer.detectAndSendChanges();
  }

  public static boolean notifyGroupOwner(org.spongepowered.api.item.inventory.ItemStack stack, Player holder, boolean joined) {
    return notifyGroupOwner(tf(stack), holder, joined);
  }

  public static boolean notifyGroupOwner(ItemStack stack, Player holder, boolean joined) {
    Validate.isTrue(isZoneSlaveItem(stack));
    Optional<Player> optPlayer = getGroupOwner(stack);
    if (optPlayer.isPresent()) {
      Player player = optPlayer.get();
      if (joined) {
        if (incrementCount(stack, player)) {
          player.sendMessage(Text.of(TextColors.GOLD, holder.getName() + " has accepted your invitation."));
        }
      } else {
        if (!isAttuned(stack) || decrementCount(stack, player)) {
          player.sendMessage(Text.of(TextColors.RED, holder.getName() + " has declined your invitation."));
        }
      }
      return true;
    }
    return false;
  }

  private static boolean incrementCount(ItemStack slaveStack, Player player) {
    NonNullList<ItemStack> itemStacks = tf(player).inventory.mainInventory;
    for (ItemStack itemStack : itemStacks) {
      if (isZoneMasterItem(itemStack) && hasSameZoneID(slaveStack, itemStack)) {
        incrementCount(itemStack);
        return true;
      }
    }
    return false;
  }

  private static boolean decrementCount(ItemStack slaveStack, Player player) {
    NonNullList<ItemStack> itemStacks = tf(player).inventory.mainInventory;
    for (ItemStack itemStack : itemStacks) {
      if (isZoneMasterItem(itemStack) && hasSameZoneID(slaveStack, itemStack)) {
        decrementCount(itemStack);
        return true;
      }
    }
    return false;
  }

  private static void incrementCount(ItemStack stack) {
    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
    tag.setInteger("zone_player_count", tag.getInteger("zone_player_count") + 1);
  }

  private static void decrementCount(ItemStack stack) {
    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
    tag.setInteger("zone_player_count", tag.getInteger("zone_player_count") - 1);
  }

  public static void rescindGroupInvite(ItemStack stack) {
    Optional<String> zone = getZone(stack);
    for (Player aPlayer : Sponge.getServer().getOnlinePlayers()) {
      NonNullList<ItemStack> itemStacks = tf(aPlayer).inventory.mainInventory;
      for (int i = 0; i < itemStacks.size(); ++i) {
        if (hasSameZoneID(stack, itemStacks.get(i)) && isZoneSlaveItem(stack)) {
          if (!zone.isPresent()) {
            aPlayer.sendMessage(Text.of(TextColors.RED, "A group you were invited to has been destroyed."));
          } else {
            aPlayer.sendMessage(Text.of(TextColors.RED, "A " + zone.get() + " group you were invited to has been destroyed."));
          }
          itemStacks.set(i, null);
        }
      }
      tf(aPlayer).inventoryContainer.detectAndSendChanges();
    }
  }

  public static boolean playerAlreadyHasInvite(org.spongepowered.api.item.inventory.ItemStack stack, Player target) {
    return playerAlreadyHasInvite(tf(stack), target);
  }

  public static boolean playerAlreadyHasInvite(ItemStack stack, Player target) {
    UUID zoneID = getZoneID(stack).orElseThrow(() -> new IllegalArgumentException("Illegal zone ItemStack"));
    NonNullList<ItemStack> inv = tf(target).inventory.mainInventory;
    for (ItemStack aStack : inv) {
      Optional<UUID> optZoneID = getZoneID(aStack);
      if (optZoneID.isPresent()) {
        if (zoneID.equals(optZoneID.get())) {
          return true;
        }
      }
    }
    return false;
  }

  public static Optional<String> getZone(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getZone(tf(stack));
  }

  public static Optional<String> getZone(ItemStack stack) {
    if (isZoneItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      return Optional.of(tag.getString("zone"));
    }
    return Optional.empty();
  }

  public static Optional<UUID> getZoneID(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getZoneID(tf(stack));
  }

  public static Optional<UUID> getZoneID(ItemStack stack) {
    if (isZoneItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      return Optional.of(UUID.fromString(tag.getString("zone_id")));
    }
    return Optional.empty();
  }

  public static boolean isZoneItem(org.spongepowered.api.item.inventory.ItemStack stack) {
    return isZoneSlaveItem(stack) || isZoneMasterItem(stack);
  }

  public static boolean isZoneItem(ItemStack stack) {
    return isZoneItem(tf(stack));
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
    return isZoneMasterItem(tf(stack));
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
    return isZoneSlaveItem(tf(stack));
  }

  public static boolean isAttuned(org.spongepowered.api.item.inventory.ItemStack stack) {
    return isAttuned(tf(stack));
  }

  public static boolean isAttuned(ItemStack stack) {
    return isZoneItem(stack) && stack.getMetadata() > 0;
  }

  public static void attune(org.spongepowered.api.item.inventory.ItemStack stack) {
    attune(tf(stack));
  }

  public static void attune(ItemStack stack) {
    if (isZoneItem(stack)) {
      stack.setItemDamage(1);
    }
  }

  public static Optional<Integer> getGroupSize(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getGroupSize(tf(stack));
  }

  public static Optional<Integer> getGroupSize(ItemStack stack) {
    if (isZoneMasterItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      return tag.hasKey("zone_player_count") ? Optional.of(tag.getInteger("zone_player_count")) : Optional.empty();
    }
    throw new IllegalArgumentException("Invalid ItemStack provided");
  }

  public static Optional<Integer> getMaxGroupSize(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getMaxGroupSize(tf(stack));
  }

  public static Optional<Integer> getMaxGroupSize(ItemStack stack) {
    if (isZoneMasterItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      int val = tag.getInteger("zone_max_players");
      return val == -1 ? Optional.empty() : Optional.of(val);
    }
    throw new IllegalArgumentException("Invalid ItemStack provided");
  }

  public static Optional<Player> getGroupOwner(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getGroupOwner(tf(stack));
  }

  public static Optional<Player> getGroupOwner(ItemStack stack) {
    if (isZoneSlaveItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      UUID ownerID = UUID.fromString(tag.getString("owner"));
      return Sponge.getServer().getPlayer(ownerID);
    }
    throw new IllegalArgumentException("Invalid ItemStack provided");
  }

  public static String getGroupOwnerName(org.spongepowered.api.item.inventory.ItemStack stack) {
    return getGroupOwnerName(tf(stack));
  }

  public static String getGroupOwnerName(ItemStack stack) {
    if (isZoneSlaveItem(stack) && hasZoneData(stack)) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      return tag.getString("owner_name");
    }
    throw new IllegalArgumentException("Invalid ItemStack provided");
  }

  public static void setMasterToZone(org.spongepowered.api.item.inventory.ItemStack stack, String zone) {
    setMasterToZone(tf(stack), zone);
  }

  public static void setMasterToZone(ItemStack stack, String zone) {
    Optional<ZoneService> optService = Sponge.getServiceManager().provide(ZoneService.class);

    if (optService.isPresent()) {
      ZoneService service = optService.get();

      if (stack.getTagCompound() == null) {
        stack.setTagCompound(new NBTTagCompound());
      }

      if (!stack.getTagCompound().hasKey("skree_zone_data")) {
        stack.getTagCompound().setTag("skree_zone_data", new NBTTagCompound());
      }

      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
      tag.setString("zone", zone);
      tag.setInteger("zone_player_count", 1);
      tag.setInteger("zone_max_players", service.getMaxGroupSize(zone).orElse(-1));
      tag.setString("zone_id", UUID.randomUUID().toString());

      attune(stack);
    }
  }

  protected static org.spongepowered.api.item.inventory.ItemStack setMasterToZoneBasic(org.spongepowered.api.item.inventory.ItemStack stack, String zone) {
    return tf(setMasterToZoneBasic(tf(stack), zone));
  }

  protected static ItemStack setMasterToZoneBasic(ItemStack stack, String zone) {

    if (stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }

    if (!stack.getTagCompound().hasKey("skree_zone_data")) {
      stack.getTagCompound().setTag("skree_zone_data", new NBTTagCompound());
    }

    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
    tag.setString("zone", zone);

    attune(stack);
    return stack;
  }

  public static boolean hasSameZoneID(org.spongepowered.api.item.inventory.ItemStack stackA, org.spongepowered.api.item.inventory.ItemStack stackB) {
    return hasSameZoneID(tf(stackA), tf(stackB));
  }

  public static boolean hasSameZoneID(ItemStack stackA, ItemStack stackB) {
    if (isZoneItem(stackA) && isZoneItem(stackB)) {
      Optional<UUID> zoneIDA = getZoneID(stackA);
      Optional<UUID> zoneIDB = getZoneID(stackB);
      return zoneIDA.isPresent() && zoneIDB.isPresent() && zoneIDA.get().equals(zoneIDB.get());
    }
    return false;
  }

  private static boolean hasZoneData(ItemStack stack) {
    return stack.getTagCompound() != null && stack.getTagCompound().hasKey("skree_zone_data");
  }

  public static org.spongepowered.api.item.inventory.ItemStack createForMaster(org.spongepowered.api.item.inventory.ItemStack inStack, Player zoneOwner) {
    Validate.isTrue(isAttuned(inStack), "Can't create a slave from an unattuned master stack.");

    ItemStack stack = new ItemStack(CustomItemTypes.ZONE_SLAVE_ORB);
    if (stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }

    if (!stack.getTagCompound().hasKey("skree_zone_data")) {
      stack.getTagCompound().setTag("skree_zone_data", new NBTTagCompound());
    }

    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_zone_data");
    tag.setString("zone", getZone(inStack).get());
    tag.setString("zone_id", getZoneID(inStack).get().toString());
    tag.setString("owner_name", zoneOwner.getName());
    tag.setString("owner", zoneOwner.getUniqueId().toString());
    return tf(stack);
  }

  public static org.spongepowered.api.item.inventory.ItemStack createForMaster(ItemStack stack, Player player) {
    return createForMaster(tf(stack), player);
  }

}
