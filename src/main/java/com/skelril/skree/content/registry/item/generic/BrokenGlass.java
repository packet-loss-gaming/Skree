/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class BrokenGlass extends CustomItem implements CookedItem, EventAwareContent {

    @Override
    public String __getID() {
        return "broken_glass";
    }

    @Override
    public List<String> __getMeshDefinitions() {
        List<String> baseList = super.__getMeshDefinitions();
        baseList.add("broken_glass_pane");
        return baseList;
    }

    @Override
    public int __getMaxStackSize() {
        return 64;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.MATERIALS;
    }

    @Override
    public void registerIngredients() {
        GameRegistry.addSmelting(new ItemStack(this, 1, 0), new ItemStack(Blocks.GLASS), 0);
        GameRegistry.addSmelting(new ItemStack(this, 1, 1), new ItemStack(Blocks.GLASS_PANE), 0);
    }

    private void dropBrokenGlass(Transaction<BlockSnapshot> block, int variant) {
        Optional<Location<World>> optOrigin = block.getOriginal().getLocation();
        if (!optOrigin.isPresent()) {
            return;
        }

        new ItemDropper(optOrigin.get()).dropStacks(
                Collections.singleton(tf(new ItemStack(this, 1, variant))),
                SpawnTypes.DROPPED_ITEM
        );
    }

    @Listener(order = Order.POST)
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
        if (!player.get(Keys.GAME_MODE).orElse(null).equals(GameModes.SURVIVAL)) {
            return;
        }

        for (Transaction<BlockSnapshot> block : event.getTransactions()) {
            BlockType originalType = block.getOriginal().getState().getType();
            if (originalType == BlockTypes.GLASS || originalType == BlockTypes.STAINED_GLASS) {
                dropBrokenGlass(block, 0);
            } else if (originalType == BlockTypes.GLASS_PANE || originalType == BlockTypes.STAINED_GLASS_PANE) {
                dropBrokenGlass(block, 1);
            }
        }
    }
}