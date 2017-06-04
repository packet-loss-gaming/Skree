/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.Teleporter;
import com.skelril.skree.content.world.WorldEntryPermissionCheck;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class FocusTeleporter extends CustomItem implements Craftable, EventAwareContent, Teleporter {

    @Override
    public String __getID() {
        return "focus_teleporter";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.TOOLS;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AAA",
                "BCB",
                "BBB",
                'A', newItemStack("skree:red_shard"),
                'B', newItemStack("skree:sea_crystal"),
                'C', newItemStack("skree:ender_focus")
        );
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.MainHand event, @First Player player) {
        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!optHeldItem.isPresent()) {
            return;
        }

        org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
        if (held.getItem() != this) {
            return;
        }

        Location<World> destination = player.getLocation();
        if (!WorldEntryPermissionCheck.checkDestination(player, destination.getExtent())) {
            player.sendMessage(Text.of(TextColors.RED, "You do not have permission to create a teleporter to this location."));
            return;
        }

        setDestination(held, destination);
        player.setItemInHand(HandTypes.MAIN_HAND, held);
        event.setCancelled(true);
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!optHeldItem.isPresent()) {
            return;
        }

        org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
        if (held.getItem() != this) {
            return;
        }

        Optional<Location<World>> optDestination = getDestination(held);
        if (!optDestination.isPresent()) {
            return;
        }

        Inventory result = player.getInventory().query((ItemType) Sponge.getRegistry().getType(ItemType.class, "skree:ender_focus").get());
        if (result.size() > 0) {
            Task.builder().execute(() -> {
                result.poll(1);
                player.setLocation(optDestination.get());
            }).delayTicks(1).submit(SkreePlugin.inst());

            event.setUseBlockResult(Tristate.FALSE);
        }
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optDestStr = getClientDestination(stack);
        tooltip.add("Destination: " + (optDestStr.isPresent() ? optDestStr.get() : "Not set"));
    }
}
