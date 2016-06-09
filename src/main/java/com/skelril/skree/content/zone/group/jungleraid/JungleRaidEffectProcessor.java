/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.jungleraid;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.entity.projectile.ThrownPotion;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class JungleRaidEffectProcessor {
    private static final Random random = new Random();

    public static void run(JungleRaidInstance inst) {
        titanMode(inst);
        distributor(inst);
        randomRockets(inst);
    }

    private static void titanMode(JungleRaidInstance inst) {
        FlagEffectData data = inst.getFlagData();
        Collection<Player> players = inst.getPlayers(PlayerClassifier.PARTICIPANT);

        if (inst.isFlagEnabled(JungleRaidFlag.TITAN_MODE) && data.titan == null) {
            Player player = Probability.pickOneOf(players);
            data.titan = player.getUniqueId();
            ItemStack teamHood = newItemStack(ItemTypes.LEATHER_HELMET);
            teamHood.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Titan Hood"));
            teamHood.offer(Keys.COLOR, Color.BLACK);
            // playerEquipment.set(EquipmentTypes.HEADWEAR, teamHood);
            tf(player).inventory.armorInventory[3] = tf(teamHood);
        }

        for (Player player : players) {
            if (inst.isFlagEnabled(JungleRaidFlag.TITAN_MODE) && player.getUniqueId().equals(data.titan)) {
                List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>());
                potionEffects.add(PotionEffect.of(PotionEffectTypes.NIGHT_VISION, 1, 20 * 20));
                player.offer(Keys.POTION_EFFECTS, potionEffects);
            }
        }
    }

    private static void distributor(JungleRaidInstance inst) {
        FlagEffectData data = inst.getFlagData();
        boolean isSuddenDeath = !inst.isFlagEnabled(JungleRaidFlag.NO_TIME_LIMIT) && System.currentTimeMillis() - inst.getStartTime() >= TimeUnit.MINUTES.toMillis(15);
        if (isSuddenDeath) {
            data.amt = 100;
        }

        if (inst.isFlagEnabled(JungleRaidFlag.END_OF_DAYS) || inst.isFlagEnabled(JungleRaidFlag.GRENADES) || inst.isFlagEnabled(JungleRaidFlag.POTION_PLUMMET) || isSuddenDeath) {

            Vector3i bvMax = inst.getRegion().getMaximumPoint();
            Vector3i bvMin = inst.getRegion().getMinimumPoint();

            for (int i = 0; i < Probability.getRangedRandom(data.amt / 3, data.amt); i++) {

                Location<World> testLoc = new Location<>(
                        inst.getRegion().getExtent(),
                        Probability.getRangedRandom(bvMin.getX(), bvMax.getX()),
                        bvMax.getY(),
                        Probability.getRangedRandom(bvMin.getZ(), bvMax.getZ())
                );

                if (testLoc.getBlockType() != BlockTypes.AIR) continue;

                if (inst.isFlagEnabled(JungleRaidFlag.END_OF_DAYS) || isSuddenDeath) {
                    Optional<Entity> optEntity = inst.getRegion().getExtent().createEntity(EntityTypes.PRIMED_TNT, testLoc.getPosition());
                    if (optEntity.isPresent()) {
                        PrimedTNT explosive = (PrimedTNT) optEntity.get();
                        explosive.setVelocity(new Vector3d(
                                random.nextDouble() * 2.0 - 1,
                                random.nextDouble() * 2 * -1,
                                random.nextDouble() * 2.0 - 1
                        ));
                        // TODO Use Sponge API after 1.9 release w/ Fuse Data merge
                        // explosive.offer(Keys.FUSE_DURATION, 20 * 4);
                        tf(explosive).setFuse(20 * 4);

                        // TODO used to have a 1/4 chance of creating fire
                        inst.getRegion().getExtent().spawnEntity(
                                explosive, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                        );
                    }
                }
                if (inst.isFlagEnabled(JungleRaidFlag.POTION_PLUMMET)) {
                    PotionEffectType type = Probability.pickOneOf(Sponge.getRegistry().getAllOf(PotionEffectType.class));
                    for (int ii = Probability.getRandom(5); ii > 0; --ii) {
                        Optional<Entity> optEntity = inst.getRegion().getExtent().createEntity(EntityTypes.SPLASH_POTION, testLoc.getPosition());
                        if (optEntity.isPresent()) {
                            ThrownPotion potion = (ThrownPotion) optEntity.get();
                            potion.setVelocity(new Vector3d(
                                    random.nextDouble() * 2.0 - 1,
                                    0,
                                    random.nextDouble() * 2.0 - 1
                            ));
                            potion.offer(Keys.POTION_EFFECTS, Lists.newArrayList(
                                    PotionEffect.of(type, 1, type.isInstant() ? 1 : 20 * 10)
                            ));
                            inst.getRegion().getExtent().spawnEntity(
                                    potion, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                            );
                        }
                    }
                }
                if (inst.isFlagEnabled(JungleRaidFlag.GRENADES)) {
                    new ItemDropper(testLoc).dropStacks(
                            Lists.newArrayList(newItemStack(ItemTypes.SNOWBALL, Probability.getRandom(3))),
                            SpawnTypes.PLUGIN
                    );
                }
            }
            if (data.amt < 150 && Probability.getChance(inst.isFlagEnabled(JungleRaidFlag.SUPER) ? 9 : 25)) ++data.amt;
        }
    }

    private static void randomRockets(JungleRaidInstance inst) {
        if (inst.isFlagEnabled(JungleRaidFlag.RANDOM_ROCKETS)) {
            for (final Player player : inst.getPlayers(PlayerClassifier.PARTICIPANT)) {
                if (!Probability.getChance(30)) continue;
                for (int i = 0; i < 5; i++) {
                    Task.builder().delayTicks(i * 4).execute(() -> {
                        Location targetLocation = player.getLocation();
                        Optional<Entity> optEntity = inst.getRegion().getExtent().createEntity(EntityTypes.FIREWORK, targetLocation.getPosition());
                        if (optEntity.isPresent()) {
                            Firework firework = (Firework) optEntity.get();
                            FireworkEffect fireworkEffect = FireworkEffect.builder()
                                    .flicker(Probability.getChance(2))
                                    .trail(Probability.getChance(2))
                                    .color(Color.RED)
                                    .fade(Color.YELLOW)
                                    .shape(FireworkShapes.BURST)
                                    .build();
                            firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(fireworkEffect));
                            firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, Probability.getRangedRandom(2, 5));
                            inst.getRegion().getExtent().spawnEntity(
                                    firework, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                            );
                        }
                    }).submit(SkreePlugin.inst());
                }
            }
        }
    }
}
