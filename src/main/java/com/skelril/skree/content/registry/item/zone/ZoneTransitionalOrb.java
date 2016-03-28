/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class ZoneTransitionalOrb extends CustomItem implements EventAwareContent, Craftable {
    @Override
    public String __getID() {
        return "zone_transitional_orb";
    }

    @Override
    public int __getMaxStackSize() {
        return 16;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabMaterials;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this, 8),
                new ItemStack(CustomItemTypes.ZONE_MASTER_ORB)
        );
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                ItemStack itemStack = tf(optItemStack.get());
                if (itemStack.getItem() == this) {
                    if (returnToMain(event, player)) {
                        return;
                    }

                    if (rejoinInstance(event, player)) {
                        return;
                    }
                }
            }
        }
    }

    private boolean returnToMain(InteractBlockEvent.Secondary event, Player player) {
        Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
        if (optWorldService.isPresent()) {
            WorldService worldService = optWorldService.get();
            WorldEffectWrapper wrapper = worldService.getEffectWrapper(InstanceWorldWrapper.class).get();

            if (wrapper.getWorlds().contains(player.getLocation().getExtent())) {
                Collection<World> worlds = optWorldService.get().getEffectWrapper(MainWorldWrapper.class).get().getWorlds();
                player.setLocation(worlds.iterator().next().getSpawnLocation());
                tf(player).inventory.decrStackSize(tf(player).inventory.currentItem, 1);
                tf(player).inventoryContainer.detectAndSendChanges();
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    private boolean rejoinInstance(InteractBlockEvent.Secondary event, Player player) {
        Optional<ZoneService> optZoneService = Sponge.getServiceManager().provide(ZoneService.class);
        if (optZoneService.isPresent()) {
            ZoneService zoneService = optZoneService.get();

            switch (zoneService.rejoin(player).getValue()) {
                case ADDED:
                    tf(player).inventory.decrStackSize(tf(player).inventory.currentItem, 1);
                    tf(player).inventoryContainer.detectAndSendChanges();
                    break;
                case NO_REJOIN:
                    player.sendMessage(Text.of(TextColors.RED, "You cannot rejoin your previous zone."));
                    break;
                case REF_LOST:
                    player.sendMessage(Text.of(TextColors.RED, "Your connection with your previous zone has been severed."));
                    break;
                case DESPAWNED:
                    player.sendMessage(Text.of(TextColors.RED, "The your previous zone has despawned."));
                    break;
                default:
                    player.sendMessage(Text.of(TextColors.RED, "Failed to rejoin your previous zone."));
                    break;
            }
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
