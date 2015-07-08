/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.google.common.base.Optional;
import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.WorldService;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

public class SkullOfTheFallen extends CustomItem implements EventAwareContent, CraftableItem {

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "BBB",
                "BAB",
                "B B",
                'A', new ItemStack(CustomItemTypes.BLOOD_DIAMOND),
                'B', new ItemStack(Items.bone)
        );
    }

    @Override
    public String __getID() {
        return "skull_of_the_fallen";
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

        if (event.getInteractionType() == EntityInteractionTypes.USE) {
            // TODO remove workaround depends on (Sponge #260)
            // BEGIN WORKAROUND
            if (event.getBlock().getX() == 0 && event.getBlock().getY() == 0 && event.getBlock().getZ() == 0 && event.getBlock().getBlockType() == BlockTypes.LOG) {
                return;
            }
            // END WORKAROUND

            Player player = event.getEntity();
            Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand();

            if (optHeldItem.isPresent()) {
                if (this.equals(optHeldItem.get().getItem())) {
                    Location pLoc = player.getLocation();

                    Optional<WorldService> optWorldService = event.getGame().getServiceManager().provide(WorldService.class);
                    if (optWorldService.isPresent()) {
                        WorldService worldService = optWorldService.get();
                        WildernessWorldWrapper wrapper = (WildernessWorldWrapper) worldService.getEffectWrapper("Wilderness");
                        if (wrapper.isApplicable(pLoc.getExtent())) {
                            int level = wrapper.getLevel(pLoc);

                            Text levelText = Texts.builder().color(TextColors.YELLOW).append(
                                    Texts.of("Wilderness level: " + level)
                            ).build();
                            Text damageText = Texts.builder().color(TextColors.YELLOW).append(
                                    Texts.of("Mob damage: +" + wrapper.getDamageMod(level))
                            ).build();
                            Text healthText = Texts.builder().color(TextColors.YELLOW).append(
                                    Texts.of("Mob health: x" + wrapper.getHealthMod(level))
                            ).build();
                            Text oreText = Texts.builder().color(TextColors.YELLOW).append(
                                    Texts.of("Ore modifier: x" + wrapper.getOreMod(level))
                            ).build();

                            player.sendMessage(levelText, damageText, healthText, oreText);
                        } else {
                            Text notWildernessText = Texts.builder().color(TextColors.RED).append(
                                    Texts.of("You're not in a Wilderness world!")
                            ).build();

                            player.sendMessage(notWildernessText);
                        }
                    }
                }
            }
        }
    }
}
