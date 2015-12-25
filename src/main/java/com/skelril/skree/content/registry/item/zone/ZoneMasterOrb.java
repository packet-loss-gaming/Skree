/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.service.ZoneService;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.*;

public class ZoneMasterOrb extends CustomItem implements EventAwareContent, Craftable {

    @Override
    public String __getID() {
        return "zone_master_orb";
    }

    @Override
    public List<String> __getMeshDefinitions() {
        List<String> baseList = super.__getMeshDefinitions();
        baseList.add("zone_master_orb_active");
        return baseList;
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabMaterials;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "BBB",
                "BAB",
                "BBB",
                'A', new ItemStack(CustomItemTypes.FAIRY_DUST),
                'B', new ItemStack(Blocks.glass)
        );
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event) {
        purgeZoneItems(event.getTargetEntity(), Optional.empty());
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event) {
        purgeZoneItems(event.getTargetEntity(), Optional.empty());
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Primary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                ItemStack itemStack = (ItemStack) (Object) optItemStack.get();
                if (isZoneMasterItem(itemStack)) {
                    if (isAttuned(itemStack)) {
                        Optional<ZoneService> optService = SkreePlugin.inst().getGame().getServiceManager().provide(ZoneService.class);
                        if (optService.isPresent()) {
                            ZoneService service = optService.get();
                            List<Player> group = new ArrayList<>();
                            group.add(player);
                            for (Player aPlayer : SkreePlugin.inst().getGame().getServer().getOnlinePlayers()) {
                                ItemStack[] itemStacks = ((EntityPlayer) aPlayer).inventory.mainInventory;
                                for (ItemStack aStack : itemStacks) {
                                    if (!hasSameZoneID(itemStack, aStack)) {
                                        continue;
                                    }

                                    if (isAttuned(aStack) && isZoneSlaveItem(aStack)) {
                                        Optional<Player> optZoneOwner = getGroupOwner(aStack);
                                        if (optZoneOwner.isPresent() && optZoneOwner.get().equals(player)) {
                                            group.add(aPlayer);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int i = 1; i < group.size(); ++i) {
                                purgeZoneItems(group.get(i), Optional.of(itemStack));
                            }
                            purgeZoneItems(group.get(0), Optional.of(itemStack));

                            service.requestZone(getZone(itemStack).get(), group);
                        }
                    } else {
                        setMasterToZone(itemStack, "Shnuggles Prime");
                        player.setItemInHand((org.spongepowered.api.item.inventory.ItemStack) (Object) itemStack);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        Entity targetEntity = event.getTargetEntity();
        if (optPlayer.isPresent() && targetEntity instanceof Player) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                org.spongepowered.api.item.inventory.ItemStack itemStack = optItemStack.get();
                if (this.equals(itemStack.getItem()) && isAttuned(itemStack)) {
                    Player targetPlayer = (Player) targetEntity;
                    if (!playerAlreadyHasInvite(itemStack, targetPlayer)) {
                        player.sendMessage(
                                Texts.of(TextColors.RED, targetPlayer.getName() + " already has an invite.")
                        );
                    } else {
                        org.spongepowered.api.item.inventory.ItemStack newStack = createForMaster(itemStack, player);
                        ((EntityPlayer) targetPlayer).inventory.addItemStackToInventory((ItemStack) (Object) newStack);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onDropItem(DropItemEvent.Dispense event) {
        event.getEntities().stream().filter(entity -> entity instanceof Item).forEach(entity -> {
            ItemStack stack = ((EntityItem) entity).getEntityItem();
            if (isZoneMasterItem(stack)) {
                rescindGroupInvite(stack);
                entity.remove();
            }
        });
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optZoneName = getZone((org.spongepowered.api.item.inventory.ItemStack) (Object) stack);
        if (optZoneName.isPresent()) {
            tooltip.add("Zone: " + optZoneName.get());
            Optional<Integer> maxPlayerCount = getMaxGroupSize(stack);
            tooltip.add("Players: " + getGroupSize(stack) + " / " + (maxPlayerCount.isPresent() ? "Unlimited" : maxPlayerCount.get()));
        }
    }
}
