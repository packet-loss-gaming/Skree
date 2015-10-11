/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.build;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import net.minecraft.entity.passive.EntityChicken;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTransaction;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.world.chunk.PopulateChunkEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.PopulatorType;

import java.util.*;

import static com.skelril.skree.content.registry.TypeCollections.ore;

public class BuildWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public BuildWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public BuildWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Build", worlds);
        this.plugin = plugin;
        this.game = game;
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        Entity entity = event.getTargetEntity();

        if (!isApplicable(entity.getWorld())) return;

        // TODO Smarter "should this mob be allowed to spawn" code
        if (entity instanceof Monster) {
            event.setCancelled(true);
        }

        if (entity instanceof EntityChicken && ((EntityChicken) entity).func_152116_bZ()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        List<BlockTransaction> transactions = event.getTransactions();
        for (BlockTransaction block : transactions) {
            Optional<Location<World>> optLoc = block.getOriginal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                continue;
            }

            if (ore().contains(block.getFinalReplacement().getState().getType())) {
                block.setCustomReplacement(block.getOriginal().withState(BlockTypes.STONE.getDefaultState()));
            }
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        List<BlockTransaction> transactions = event.getTransactions();
        for (BlockTransaction block : transactions) {
            BlockSnapshot finalReplacement = block.getFinalReplacement();

            Optional<Location<World>> optLoc = finalReplacement.getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                continue;
            }

            Location loc = optLoc.get();

            BlockType replacementType = finalReplacement.getState().getType();
            if (ore().contains(replacementType)) {
                Optional<?> rootCause = event.getCause().root();
                if (rootCause.isPresent() && rootCause.get() instanceof Player) {
                    Player player = (Player) rootCause.get();

                    // Allow creative mode players to still place blocks
                    if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                        continue;
                    }

                    try {
                        Vector3d origin = loc.getPosition();
                        World world = toWorld.from(loc.getExtent());
                        for (int i = 0; i < 40; ++i) {
                            ParticleEffect effect = game.getRegistry().createParticleEffectBuilder(
                                    ParticleTypes.CRIT_MAGIC
                            ).motion(
                                    new Vector3d(
                                            Probability.getRangedRandom(-1, 1),
                                            Probability.getRangedRandom(-.7, .7),
                                            Probability.getRangedRandom(-1, 1)
                                    )
                            ).count(1).build();

                            world.spawnParticles(effect, origin.add(.5, .5, .5));
                        }
                    } catch (Exception ex) {
                        player.sendMessage(
                                /* ChatTypes.SYSTEM, */
                                Texts.of(
                                        TextColors.RED,
                                        "You find yourself unable to place that block."
                                )
                        );
                    }
                    block.setIsValid(false);
                }
            }
        }
    }

    @Listener
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        for (Map.Entry<PopulatorType, List<BlockTransaction>> entry : event.getPopulatedTransactions().entrySet()) {
            for (BlockTransaction transaction : entry.getValue()) {
                BlockSnapshot finalReplacement = transaction.getFinalReplacement();

                Optional<Location<World>> optLoc = finalReplacement.getLocation();

                if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                    continue;
                }

                BlockType replacementType = finalReplacement.getState().getType();
                if (ore().contains(replacementType)) {
                    BlockSnapshot stone = transaction.getOriginal().withState(BlockTypes.STONE.getDefaultState());
                    transaction.setCustomReplacement(stone);
                }
            }
        }
    }
}