/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

import java.util.List;
import java.util.Optional;

import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.*;

public class ZoneSlaveOrb extends CustomItem implements EventAwareContent {

    @Override
    public String __getID() {
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

    @Listener
    public void onDropItem(DropItemEvent.Dispense event) {
        event.getEntities().stream().filter(entity -> entity instanceof Item).forEach(entity -> {
            if (isZoneSlaveItem(((EntityItem) entity).getEntityItem())) {
                entity.remove();
            }
        });
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Primary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optItemStack = player.getItemInHand();
            if (optItemStack.isPresent()) {
                org.spongepowered.api.item.inventory.ItemStack itemStack = optItemStack.get();
                if (isZoneSlaveItem(itemStack)) {
                    attune(itemStack);
                    event.setCancelled(true);
                }
            }
        }
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optZoneName = getZone((org.spongepowered.api.item.inventory.ItemStack) (Object) stack);
        if (optZoneName.isPresent()) {
            tooltip.add("Zone: " + optZoneName.get());
        }

        // If there's an invalid item stack don't crash the client
        try {
            tooltip.add("Group owner: " + getGroupOwnerName((org.spongepowered.api.item.inventory.ItemStack) (Object) stack));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}