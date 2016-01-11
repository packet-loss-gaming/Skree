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
import com.skelril.skree.service.ZoneService;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
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
        GameRegistry.addRecipe(new ZoneMasterOrbRecipie(
                "Shnuggles Prime",
                new ItemStack(this),
                new ItemStack(Items.rotten_flesh)
        ));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(net.minecraft.item.Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event) {
        purgeZoneItems(event.getTargetEntity(), Optional.empty());
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                ItemStack itemStack = tf(optItemStack.get());
                if (isZoneMasterItem(itemStack)) {
                    if (isAttuned(itemStack)) {
                        Optional<ZoneService> optService = Sponge.getServiceManager().provide(ZoneService.class);
                        if (optService.isPresent()) {
                            ZoneService service = optService.get();
                            List<Player> group = new ArrayList<>();
                            group.add(player);
                            for (Player aPlayer : Sponge.getServer().getOnlinePlayers()) {
                                ItemStack[] itemStacks = tf(aPlayer).inventory.mainInventory;
                                for (ItemStack aStack : itemStacks) {
                                    if (!hasSameZoneID(itemStack, aStack)) {
                                        continue;
                                    }

                                    if (isAttuned(aStack) && isZoneSlaveItem(aStack)) {
                                        Optional<Player> optZoneOwner = getGroupOwner(aStack);
                                        if (optZoneOwner.isPresent()) {
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
                    if (playerAlreadyHasInvite(itemStack, targetPlayer)) {
                        player.sendMessage(
                                Text.of(TextColors.RED, targetPlayer.getName() + " already has an invite.")
                        );
                    } else {
                        org.spongepowered.api.item.inventory.ItemStack newStack = createForMaster(itemStack, player);
                        tf(targetPlayer).inventory.addItemStackToInventory(tf(newStack));
                        player.sendMessage(
                                Text.of(TextColors.GOLD, targetPlayer.getName() + " has been given invite.")
                        );
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
            if (isZoneMasterItem(stack) && isAttuned(stack)) {
                rescindGroupInvite(stack);
                ItemStack reset = new ItemStack(CustomItemTypes.ZONE_MASTER_ORB);
                setMasterToZone(reset, getZone(stack).get());
                entity.offer(Keys.REPRESENTED_ITEM, tf(reset).createSnapshot());
            }
        });
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optZoneName = getZone(tf(stack));
        if (optZoneName.isPresent()) {
            tooltip.add("Zone: " + optZoneName.get());
            Optional<Integer> playerCount = getGroupSize(stack);
            if (playerCount.isPresent()) {
                Optional<Integer> maxPlayerCount = getMaxGroupSize(stack);
                tooltip.add("Players: " + playerCount.get() + " / " + (!maxPlayerCount.isPresent() ? "Unlimited" : maxPlayerCount.get()));
            }
        }
    }
}
