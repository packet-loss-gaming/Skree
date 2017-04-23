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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
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
                'A', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:red_shard").get()),
                'B', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:sea_crystal").get()),
                'C', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:ender_focus").get())
        );
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                Location<World> destination = player.getLocation();
                if (!WorldEntryPermissionCheck.checkDestination(player, destination.getExtent())) {
                    player.sendMessage(Text.of(TextColors.RED, "You do not have permission to create a teleporter to this location."));
                    return;
                }

                setDestination(held, destination);
                player.setItemInHand(HandTypes.MAIN_HAND, held);
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                Optional<Location<World>> optDestination = getDestination(held);
                if (optDestination.isPresent()) {
                    Inventory result = player.getInventory().query((ItemType) (Item) Sponge.getRegistry().getType(ItemType.class, "skree:ender_focus").get());
                    if (result.size() > 0) {
                        Task.builder().execute(() -> {
                            result.poll(1);
                            player.setLocation(optDestination.get());
                        }).delayTicks(1).submit(SkreePlugin.inst());

                        event.setUseBlockResult(Tristate.FALSE);
                    }
                }
            }
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
