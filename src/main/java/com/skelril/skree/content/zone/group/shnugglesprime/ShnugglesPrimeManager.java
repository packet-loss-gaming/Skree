/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.droptable.CofferResolver;
import com.skelril.skree.content.zone.*;
import com.skelril.skree.content.zone.group.shnugglesprime.ShnugglesPrimeInstance.AttackSeverity;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Giant;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.skelril.nitro.entity.EntityHealthUtil.*;
import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.item.CustomItemTypes.SCROLL_OF_SUMMATION;

public class ShnugglesPrimeManager extends GroupZoneManager<ShnugglesPrimeInstance> implements Runnable, LocationZone<ShnugglesPrimeInstance> {
    private static final DropTable dropTable;

    static {
        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller();
        dropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(new CofferResolver(100), 1)
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:fairy_dust")
                                                        )
                                                ), 25
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.DIAMOND)
                                                        )
                                                ), 35
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:jurack")
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:sea_crystal")
                                                        )
                                                ), 100
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:emblem_of_the_forge")
                                                        )
                                                ), 750
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:holy_hilt")
                                                        )
                                                ), 15000
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:holy_blade")
                                                        )
                                                ), 15000
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:emblem_of_hallow")
                                                        )
                                                ), 750
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:demonic_hilt")
                                                        )
                                                ), 15000
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:demonic_blade")
                                                        )
                                                ), 15000
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(SCROLL_OF_SUMMATION, 3)
                                                        )
                                                ), 75
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:ancient_metal_fragment")
                                                        )
                                                ), 250
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:ancient_ingot")
                                                        )
                                                ), 500
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:unstable_catalyst")
                                                        )
                                                ), 25000
                                        )
                                )
                        )
                )
        );
    }

    private final BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager = new BossManager<>();

    public ShnugglesPrimeManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ShnugglesPrimeListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneNaturalSpawnBlocker<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZonePvPListener<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneInventoryProtector<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneImmutableBlockListener<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneCreatureDropBlocker<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneTransitionalOrbListener<>(this::getApplicableZone)
        );

        setupBossManager();
        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private void setupBossManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(bossManager, Giant.class)
        );

        List<Instruction<BindCondition, Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>>> bindProcessor = bossManager.getBindProcessor();
        bindProcessor.add((condition, boss) -> {
            Optional<Giant> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Giant bossEnt = optBossEnt.get();
                bossEnt.offer(Keys.DISPLAY_NAME, Text.of("Shnuggles Prime"));
                setMaxHealth(bossEnt, 750, true);
            }
            return Optional.empty();
        });
        bindProcessor.add((condition, boss) -> {
            Optional<Giant> optBoss = boss.getTargetEntity();
            if (optBoss.isPresent()) {
                optBoss.get().offer(Keys.PERSISTS, true);
            }
            return Optional.empty();
        });
        bindProcessor.add((condition, boss) -> {
            boss.getDetail().getZone().getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(Text.of(TextColors.GOLD, "I live again!"));
            return Optional.empty();
        });

        List<Instruction<UnbindCondition, Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>>> unbindProcessor = bossManager.getUnbindProcessor();
        unbindProcessor.add((condition, boss) -> {
            ShnugglesPrimeInstance inst = boss.getDetail().getZone();

            // Reset respawn mechanics
            inst.bossDied();
            // Buff babies
            inst.buffBabies();

            return Optional.empty();
        });
        unbindProcessor.add((condition, boss) -> {
            ShnugglesPrimeInstance inst = boss.getDetail().getZone();

            int playerCount = inst.getPlayers(PlayerClassifier.PARTICIPANT).size();

            Collection<ItemStack> drops = dropTable.getDrops(
                    Math.min(7000, playerCount * 1500),
                    1
            );

            Optional<Giant> optEnt = boss.getTargetEntity();
            if (optEnt.isPresent()) {
                Task.builder().execute(() -> {
                    new ItemDropper(optEnt.get().getLocation()).dropStacks(drops, SpawnTypes.DROPPED_ITEM);
                }).delayTicks(1).submit(SkreePlugin.inst());
            }

            return Optional.empty();
        });

        List<Instruction<DamagedCondition, Boss<Giant, ZoneBossDetail<ShnugglesPrimeInstance>>>> damagedProcessor = bossManager.getDamagedProcessor();
        damagedProcessor.add((condition, boss) -> {
            ShnugglesPrimeInstance inst = boss.getDetail().getZone();
            DamageEntityEvent event = condition.getEvent();
            // Schedule a task to change the display name to show HP
            Task.builder().execute(inst::printBossHealth).delayTicks(1).submit(SkreePlugin.inst());
            if (inst.damageHeals()) {
                if (inst.isActiveAttack(ShnugglesPrimeAttack.BASK_IN_MY_GLORY)) {
                    if (boss.getTargetEntity().isPresent()) {
                        toFullHealth(boss.getTargetEntity().get());
                    }
                } else {
                    double healedDamage = event.getFinalDamage() * 2;
                    if (boss.getTargetEntity().isPresent()) {
                        heal(boss.getTargetEntity().get(), healedDamage);
                    }
                }
                event.setBaseDamage(0);

                if (Probability.getChance(3) && event.getCause().first(Player.class).isPresent()) {
                    int affected = 0;
                    if (boss.getTargetEntity().isPresent()) {
                        for (Entity e : boss.getTargetEntity().get().getNearbyEntities(8)) {
                            if (e.isLoaded() && !e.isRemoved() && e instanceof Player && inst.contains(e)) {
                                e.setVelocity(new Vector3d(
                                        Math.random() * 3 - 1.5,
                                        Math.random() * 4,
                                        Math.random() * 3 - 1.5
                                ));
                                e.offer(Keys.FIRE_TICKS, Probability.getRandom(20 * 60));
                                ++affected;
                            }
                        }
                    }

                    if (affected > 0) {
                        inst.sendAttackBroadcast("Feel my power!", AttackSeverity.INFO);
                    }
                }
            }

            Optional<DamageSource> optDmgSource = condition.getDamageSource();
            if (optDmgSource.isPresent()) {
                DamageSource dmgSource = optDmgSource.get();

                Entity attacker = null;
                if (dmgSource instanceof IndirectEntityDamageSource) {
                    attacker = ((IndirectEntityDamageSource) dmgSource).getIndirectSource();
                } else if (dmgSource instanceof EntityDamageSource) {
                    attacker = ((EntityDamageSource) dmgSource).getSource();

                    if (attacker instanceof Player) {
                    /* TODO Convert to Sponge
                    if (ItemUtil.hasForgeBook((Player) attacker)) {
                        boss.setHealth(0);
                        final Player finalAttacker = (Player) attacker;
                        if (!finalAttacker.getGameMode().equals(GameMode.CREATIVE)) {
                            server().getScheduler().runTaskLater(inst(), () -> (finalAttacker).setItemInHand(null), 1);
                        }
                    }*/
                    }
                }

                if (Probability.getChance(3) && attacker instanceof Player) {
                    inst.spawnMinions((Player) attacker);
                }
            }

            return Optional.empty();
        });
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<ShnugglesPrimeInstance>> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            ShnugglesPrimeInstance instance = new ShnugglesPrimeInstance(region, bossManager);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));
        });
    }

    @Override
    public String getName() {
        return "Shnuggles Prime";
    }

    @Override
    public void run() {
        Iterator<ShnugglesPrimeInstance> it = zones.iterator();
        while (it.hasNext()) {
            ShnugglesPrimeInstance next = it.next();
            if (next.isActive()) {
                next.run();
                continue;
            }
            next.forceEnd();

            Optional<ZoneSpaceAllocator> optAllocator = next.getRegion().getAllocator();
            if (optAllocator.isPresent()) {
                optAllocator.get().release(getSystemName(), next.getRegion());
            }

            it.remove();
        }
    }
}
