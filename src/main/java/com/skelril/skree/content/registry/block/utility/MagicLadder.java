/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.utility;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;

public class MagicLadder extends BlockLadder implements ICustomBlock, EventAwareContent, Craftable {

    public MagicLadder() {
        super();

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(0.4F);
        this.setStepSound(soundTypeLadder);
    }

    @Override
    public String __getID() {
        return "magic_ladder";
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this),
                new ItemStack(Blocks.ladder),
                new ItemStack(CustomItemTypes.FAIRY_DUST)
        );
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        boolean survival = true;
        if (optPlayer.isPresent()) {
            Value<GameMode> valGameMode = optPlayer.get().getGameModeData().type();
            if (valGameMode.exists() && !valGameMode.get().equals(GameModes.SURVIVAL)) {
                survival = false;
            }
        }

        for (Transaction<BlockSnapshot> block : event.getTransactions()) {
            BlockType originalType = block.getOriginal().getState().getType();
            if (originalType == this) {
                Optional<Location<World>> optLoc = block.getOriginal().getLocation();
                if (optLoc.isPresent()) {
                    Location<World> loc = optLoc.get();
                    MagicBlockStateHelper.startLadder(loc);

                    if (!survival) {
                        continue;
                    }

                    MagicBlockStateHelper.dropItems(loc, event.getCause());
                }
            }
        }
    }
}
