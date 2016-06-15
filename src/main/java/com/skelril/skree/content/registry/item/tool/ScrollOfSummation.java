/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.google.common.collect.ImmutableList;
import com.skelril.nitro.item.ItemCompactor;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;
import com.skelril.skree.content.registry.item.generic.*;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class ScrollOfSummation extends CustomItem implements EventAwareContent {

    @Override
    public String __getID() {
        return "scroll_of_summation";
    }

    @Override
    public int __getMaxStackSize() {
        return 64;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.TOOLS;
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                net.minecraft.item.ItemStack[] pInv = tf(player).inventory.mainInventory;
                Optional<ItemStack[]> optCompacted = new ItemCompactor(ImmutableList.of(
                        CoalValueMap.inst(),
                        IronValueMap.inst(),
                        GoldValueMap.inst(),
                        RedstoneValueMap.inst(),
                        LapisValueMap.inst(),
                        DiamondValueMap.inst(),
                        EmeraldValueMap.inst(),
                        CofferValueMap.inst()
                )).execute((ItemStack[]) (Object[]) pInv);

                if (optCompacted.isPresent()) {
                    ItemStack[] nInv = optCompacted.get();
                    for (int i = 0; i < pInv.length; ++i) {
                        pInv[i] = tf(nInv[i]);
                    }
                    tf(player).inventoryContainer.detectAndSendChanges();
                    tf(player).inventory.decrStackSize(tf(player).inventory.currentItem, 1);
                    player.sendMessage(Text.of(TextColors.GOLD, "The scroll glows brightly before turning to dust..."));
                }
            }
        }
    }
}