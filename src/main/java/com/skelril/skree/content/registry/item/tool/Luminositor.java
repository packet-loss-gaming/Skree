/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.data.util.LightLevelUtil;
import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Luminositor extends CustomItem implements EventAwareContent, Craftable {

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

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
        if (SkreePlugin.inst().getGame().getPlatform().getExecutionType().isClient()) return;

        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<ItemStack> optHeldItem = player.getItemInHand();

        if (optHeldItem.isPresent() /* && optClickedPosition.isPresent() */) {
            if (this.equals(optHeldItem.get().getItem())) {
                Direction dir = event.getTargetSide();
                Optional<Location<World>> optTargetBlockLoc = event.getTargetBlock().getLocation();

                if (!optTargetBlockLoc.isPresent()) {
                    return;
                }

                Location<World> targetBlockLoc = optTargetBlockLoc.get();
                Vector3i targPos = targetBlockLoc.getBlockPosition().add(dir.toVector3d().toInt());
                Location<World> trueTargBlock = new Location<>(targetBlockLoc.getExtent(), targPos);

                int lightLevel = LightLevelUtil.getMaxLightLevel(trueTargBlock).get();

                TextColor color;
                if (lightLevel >= 12) {
                    color = TextColors.GREEN;
                } else if (lightLevel >= 8) {
                    color = TextColors.RED;
                } else {
                    color = TextColors.DARK_RED;
                }

                // TODO system message.color(color)
                player.sendMessage(Texts.of(TextColors.YELLOW, "Light level: ", color, lightLevel));
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