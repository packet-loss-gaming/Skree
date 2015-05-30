/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.google.common.base.Optional;
import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

public class Luminositor extends Item implements CustomItem {

    public Luminositor() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public String getID() {
        return "luminositor";
    }

    @Subscribe
    public void onRightClick(PlayerInteractBlockEvent event) {
        // TODO needs right click support & light level fetching support
        if (event.getInteractionType() == EntityInteractionTypes.USE) {
            Player player = event.getEntity();
            //Optional<Vector3d> optClickedPosition = event.getClickedPosition();
            Optional<ItemStack> optHeldItem = player.getItemInHand();

            if (optHeldItem.isPresent() /* && optClickedPosition.isPresent() */) {
                if (this.equals(optHeldItem.get().getItem())) {
                    Location pLoc = player.getLocation();

                    int lightLevel = pLoc.getLuminance();

                    TextColor color;
                    if (lightLevel >= 12) {
                        color = TextColors.GREEN;
                    } else if (lightLevel >= 8) {
                        color = TextColors.RED;
                    } else {
                        color = TextColors.DARK_RED;
                    }

                    Text message = Texts.builder().color(TextColors.YELLOW).append(
                            Texts.of("Light level: ")
                    ).build();
                    // TODO system message.color(color)
                    player.sendMessage(
                            Texts.of(
                                    message,
                                    Texts.builder().color(color).append(Texts.of(lightLevel)).build()
                            )
                    );
                }
            }
        }
    }
}