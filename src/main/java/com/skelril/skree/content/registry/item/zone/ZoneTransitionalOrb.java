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
import com.skelril.skree.service.RespawnService;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
        return CreativeTabs.MATERIALS;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this, 8),
                new ItemStack(CustomItemTypes.ZONE_MASTER_ORB)
        );
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
            if (optItemStack.isPresent()) {
                ItemStack itemStack = tf(optItemStack.get());
                if (itemStack.getItem() == this) {
                    if (rejoinInstance(player)) {
                        event.setUseBlockResult(Tristate.FALSE);
                        event.setUseItemResult(Tristate.FALSE);
                    }
                }
            }
        }
    }

    private boolean isInInstanceWorld(Player player) {
        Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
        if (!optWorldService.isPresent()) {
            return false;
        }

        WorldService worldService = optWorldService.get();
        WorldEffectWrapper wrapper = worldService.getEffectWrapper(InstanceWorldWrapper.class).get();

        return wrapper.getWorlds().contains(player.getLocation().getExtent());
    }

    private void saveLocation(Player player, Location<World> location) {
        RespawnService respawnService = Sponge.getServiceManager().provideUnchecked(RespawnService.class);
        respawnService.push(player, location);
    }

    private boolean rejoinInstance(Player player) {
        Optional<ZoneService> optZoneService = Sponge.getServiceManager().provide(ZoneService.class);
        if (!optZoneService.isPresent()) {
            return false;
        }

        if (isInInstanceWorld(player)) {
            return false;
        }

        Location<World> priorLocation = player.getLocation();

        ZoneService zoneService = optZoneService.get();
        switch (zoneService.rejoin(player).getValue()) {
            case ADDED:
                saveLocation(player, priorLocation);
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

        return true;
    }
}
