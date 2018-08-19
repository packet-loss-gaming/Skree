/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.*;

public class ZoneSlaveOrb extends CustomItem implements EventAwareContent {

  @Override
  public String __getId() {
    return "zone_slave_orb";
  }

  @Override
  public List<String> __getMeshDefinitions() {
    List<String> baseList = super.__getMeshDefinitions();
    baseList.add("zone_slave_orb_active");
    return baseList;
  }

  @Override
  public int __getMaxStackSize() {
    return 1;
  }

  @Override
  public CreativeTabs __getCreativeTab() {
    return null;
  }

  @Override
  public String getHighlightTip(ItemStack item, String displayName) {
    Optional<String> optContained = getZone(item);

    return optContained.isPresent() ? optContained.get() + " " + displayName : displayName;
  }

  @Listener
  public void onDropItem(DropItemEvent.Dispense event) {
    event.getEntities().stream().filter(entity -> entity instanceof Item).forEach(entity -> {
      ItemStack stack = ((EntityItem) entity).getItem();
      if (isZoneSlaveItem(stack)) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
          Player player = optPlayer.get();
          if (!notifyGroupOwner(stack, player, false)) {
            // TODO Log this, as it shouldn't happen
          }
          player.sendMessage(
              Text.of(TextColors.RED, "You've declined your group invite.")
          );
        }
        entity.remove();
      }
    });
  }

  @Listener
  public void onBlockInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
    Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
    if (!optItemStack.isPresent()) {
      return;
    }

    org.spongepowered.api.item.inventory.ItemStack itemStack = optItemStack.get();
    if (isZoneSlaveItem(itemStack)) {
      if (!isAttuned(itemStack)) {
        Task.builder().execute(() -> {
          if (notifyGroupOwner(itemStack, player, true)) {
            attune(itemStack);
            player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
            player.sendMessage(Text.of(TextColors.GOLD, "You've accepted your group invite."));
          }
        }).delayTicks(1).submit(SkreePlugin.inst());
      } else {
        player.sendMessage(Text.of(TextColors.RED, "You've already accepted your group invite."));
      }
      event.setUseBlockResult(Tristate.FALSE);
    }
  }

  // Modified Native Item methods

  @SuppressWarnings("unchecked")
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable net.minecraft.world.World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    Optional<String> optZoneName = getZone(stack);
    if (optZoneName.isPresent()) {
      tooltip.add("Zone: " + optZoneName.get());
    }

    // If there's an invalid item stack don't crash the client
    try {
      tooltip.add("Group owner: " + getGroupOwnerName(stack));
    } catch (Exception ignored) {
      // Client side error, don't spam the log
    }
  }
}