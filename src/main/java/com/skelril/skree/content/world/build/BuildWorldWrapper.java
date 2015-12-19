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
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
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
        List<Entity> entities = event.getEntities();

        for (Entity entity : entities) {
            if (!isApplicable(entity)) continue;

            if (entity instanceof Monster) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
        for (Transaction<BlockSnapshot> block : transactions) {
            Optional<Location<World>> optLoc = block.getOriginal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                continue;
            }

            if (ore().contains(block.getFinal().getState().getType())) {
                block.setCustom(block.getOriginal().withState(BlockTypes.STONE.getDefaultState()));
            }
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
        for (Transaction<BlockSnapshot> block : transactions) {
            BlockSnapshot finalReplacement = block.getFinal();

            Optional<Location<World>> optLoc = finalReplacement.getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                continue;
            }

            Location<World> loc = optLoc.get();

            BlockType replacementType = finalReplacement.getState().getType();
            if (ore().contains(replacementType)) {
                Optional<Player> optPlayer = event.getCause().first(Player.class);
                if (optPlayer.isPresent()) {
                    Player player = optPlayer.get();

                    // Allow creative mode players to still place blocks
                    if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                        continue;
                    }

                    try {
                        Vector3d origin = loc.getPosition();
                        World world = loc.getExtent();
                        for (int i = 0; i < 40; ++i) {
                            ParticleEffect effect = game.getRegistry().createBuilder(
                                    ParticleEffect.Builder.class
                            ).type(
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
                    block.setValid(false);
                }
            }
        }
    }

    @Listener
    public void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        for (Map.Entry<PopulatorType, List<Transaction<BlockSnapshot>>> entry : event.getPopulatedTransactions().entrySet()) {
            for (Transaction<BlockSnapshot> transaction : entry.getValue()) {
                BlockSnapshot finalReplacement = transaction.getFinal();

                Optional<Location<World>> optLoc = finalReplacement.getLocation();

                if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                    continue;
                }

                BlockType replacementType = finalReplacement.getState().getType();
                if (ore().contains(replacementType)) {
                    BlockSnapshot stone = transaction.getOriginal().withState(BlockTypes.STONE.getDefaultState());
                    transaction.setCustom(stone);
                }
            }
        }
    }
}