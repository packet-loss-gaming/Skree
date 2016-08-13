/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;


import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.WorldService;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.DecimalFormat;
import java.util.Optional;

public class SkullOfTheFallen extends CustomItem implements EventAwareContent, Craftable {

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new net.minecraft.item.ItemStack(this),
                "BBB",
                "BAB",
                "B B",
                'A', new net.minecraft.item.ItemStack(CustomItemTypes.BLOOD_DIAMOND),
                'B', new net.minecraft.item.ItemStack(Items.BONE)
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
        return CreativeTabs.TOOLS;
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();


        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            if (this.equals(optHeldItem.get().getItem())) {
                Location<World> pLoc = player.getLocation();

                Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
                if (optWorldService.isPresent()) {
                    WorldService worldService = optWorldService.get();
                    WildernessWorldWrapper wrapper = worldService.getEffectWrapper(WildernessWorldWrapper.class).get();
                    Optional<Integer> optLevel = wrapper.getLevel(pLoc);
                    if (optLevel.isPresent()) {
                        int level = optLevel.get();

                        DecimalFormat df = new DecimalFormat("#,###.##");

                        player.sendMessages(
                            Text.of(TextColors.YELLOW, "Wilderness level: " + level),
                            Text.of(TextColors.YELLOW, "PvP Enabled: " + (wrapper.allowsPvP(level) ? "Yes" : "No")),
                            Text.of(TextColors.YELLOW, "Mob damage: +" + df.format(wrapper.getDamageMod(level))),
                            Text.of(TextColors.YELLOW, "Mob health: x" + df.format(wrapper.getHealthMod(level))),
                            Text.of(TextColors.YELLOW, "Ore modifier: x" + df.format(wrapper.getOreMod(wrapper.getDropTier(level)))),
                            Text.of(TextColors.YELLOW, "Drop modifier: x" + df.format(level * wrapper.getDropMod(wrapper.getDropTier(level))))
                        );
                    } else {
                        player.sendMessage(Text.of(TextColors.RED, "You're not in a Wilderness world!"));
                    }
                    event.setUseBlockResult(Tristate.FALSE);
                }
            }
        }
    }
}
