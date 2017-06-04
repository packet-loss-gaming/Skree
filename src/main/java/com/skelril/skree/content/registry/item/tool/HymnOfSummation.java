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
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;
import com.skelril.skree.content.registry.item.generic.*;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class HymnOfSummation extends CustomItem implements EventAwareContent {

    @Override
    public String __getID() {
        return "hymn_of_summation";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.TOOLS;
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (!optHeldItem.isPresent()) {
            return;
        }

        ItemStack held = optHeldItem.get();
        if (held.getItem() != this) {
            return;
        }

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
            Task.builder().execute(() -> {
                ItemStack[] nInv = optCompacted.get();
                for (int i = 0; i < pInv.length; ++i) {
                    pInv[i] = tf(nInv[i]);
                }
                tf(player).inventoryContainer.detectAndSendChanges();
                player.sendMessage(Text.of(TextColors.GOLD, "The hymn glows brightly..."));
            }).delayTicks(1).submit(SkreePlugin.inst());

            event.setUseBlockResult(Tristate.FALSE);
        }
    }
}