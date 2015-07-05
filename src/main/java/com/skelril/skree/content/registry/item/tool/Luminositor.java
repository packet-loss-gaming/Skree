/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.google.common.base.Optional;
import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.NitroItem;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.block.BlockTypes;
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

import java.lang.reflect.InvocationTargetException;
<<<<<<< HEAD:src/main/java/com/skelril/skree/content/registry/item/generic/Luminositor.java
public class Luminositor extends Item implements CustomItem, EventAwareContent, CraftableItem {
=======

public class Luminositor extends NitroItem implements EventAwareContent, CraftableItem {
>>>>>>> origin/master:src/main/java/com/skelril/skree/content/registry/item/tool/Luminositor.java

    @Override
    public String __getID() {
        return "luminositor";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }

    @Subscribe
    public void onRightClick(PlayerInteractBlockEvent event) {
        if (event.getGame().getPlatform().getExecutionType().isClient()) return;

        // TODO needs right click support
        if (event.getInteractionType() == EntityInteractionTypes.USE) {
            // TODO remove workaround depends on (Sponge #260)
            // BEGIN WORKAROUND
            if (event.getBlock().getX() == 0 && event.getBlock().getY() == 0 && event.getBlock().getZ() == 0 && event.getBlock().getBlockType() == BlockTypes.LOG) {
                return;
            }
            // END WORKAROUND

            Player player = event.getEntity();
            //Optional<Vector3d> optClickedPosition = event.getClickedPosition();
            Optional<ItemStack> optHeldItem = player.getItemInHand();

            if (optHeldItem.isPresent() /* && optClickedPosition.isPresent() */) {
                if (this.equals(optHeldItem.get().getItem())) {
                    Location pLoc = player.getLocation();

                    // TODO Remove temporary workaround
                    int lightLevel;
                    try {
                        lightLevel = (int) pLoc.getClass().getMethod("getLuminance").invoke(pLoc);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        lightLevel = 0;
                    }

                    // int lightLevel = pLoc.getLuminance();

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

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new net.minecraft.item.ItemStack(this),
                "ABA",
                " C ",
                " C ",
                'A', new net.minecraft.item.ItemStack(Items.glowstone_dust),
                'B', new net.minecraft.item.ItemStack(Items.redstone),
                'C', new net.minecraft.item.ItemStack(Items.iron_ingot)
        );
    }
}