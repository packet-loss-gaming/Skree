/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.Clause;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
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
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.Consumer;

import static com.skelril.nitro.entity.EntityHealthUtil.*;

public class ShnugglesPrimeManager extends GroupZoneManager<ShnugglesPrimeInstance> implements Runnable, LocationZone<ShnugglesPrimeInstance> {

    private final Queue<ZoneRegion> freeRegions = new LinkedList<>();
    private final BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager = new BossManager<>();

    public ShnugglesPrimeManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ShnugglesPrimeListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneNaturalSpawnBlocker(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZonePvPListener(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneInventoryProtector(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneImmutableBlockListener(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneCreatureDropBlocker(a -> getApplicableZone(a).isPresent())
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

                if (dmgSource instanceof EntityDamageSource) {
                    Entity attacker = ((EntityDamageSource) dmgSource).getSource();
                    if (Probability.getChance(3) && attacker instanceof Player) {
                        inst.spawnMinions((Player) attacker);
                    }
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
            }

            return Optional.empty();
        });
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<ShnugglesPrimeInstance>> callback) {
        Consumer<Clause<ZoneRegion, ZoneRegion.State>> consumer = clause -> {
            ZoneRegion region = clause.getKey();

            ShnugglesPrimeInstance instance = new ShnugglesPrimeInstance(region, bossManager);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));
        };

        ZoneRegion region = freeRegions.poll();
        if (region == null) {
            allocator.regionFor(getSystemName(), consumer);
        } else {
            consumer.accept(new Clause<>(region, ZoneRegion.State.RELOADED));
        }
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
            freeRegions.add(next.getRegion());
            it.remove();
        }
    }
}
