/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.patientx;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.Clause;
import com.skelril.nitro.entity.VelocityEntitySpawner;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.*;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.skelril.nitro.entity.EntityHealthUtil.setMaxHealth;

public class PatientXManager extends GroupZoneManager<PatientXInstance> implements Runnable, LocationZone<PatientXInstance> {
    private final BossManager<Zombie, ZoneBossDetail<PatientXInstance>> bossManager = new BossManager<>();
    private final PatientXConfig config = new PatientXConfig();

    public PatientXManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new PatientXListener(this)
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
                new ZoneCreatureDropBlocker(a -> getApplicableZone(a).isPresent())
        );

        setupBossManager();
        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private static Set<DamageType> blockedDamage = new HashSet<>();

    static {
        blockedDamage.add(DamageTypes.EXPLOSIVE);
    }

    public Set<DamageType> getBlockedDamage() {
        return Collections.unmodifiableSet(blockedDamage);
    }

    private void setupBossManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(bossManager, Zombie.class)
        );

        List<Instruction<BindCondition, Boss<Zombie, ZoneBossDetail<PatientXInstance>>>> bindProcessor = bossManager.getBindProcessor();
        bindProcessor.add((condition, boss) -> {
            Optional<Zombie> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Zombie bossEnt = optBossEnt.get();
                bossEnt.offer(Keys.DISPLAY_NAME, Text.of("Patient X"));
                setMaxHealth(bossEnt, config.bossHealth, true);
            }
            return Optional.empty();
        });
        bindProcessor.add((condition, boss) -> {
            Optional<Zombie> optBoss = boss.getTargetEntity();
            if (optBoss.isPresent()) {
                optBoss.get().offer(Keys.PERSISTS, true);
            }
            return Optional.empty();
        });
        bindProcessor.add((condition, boss) -> {
            boss.getDetail().getZone().getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(Text.of(TextColors.GOLD, "Ice to meet you again!"));
            return Optional.empty();
        });

        List<Instruction<UnbindCondition, Boss<Zombie, ZoneBossDetail<PatientXInstance>>>> unbindProcessor = bossManager.getUnbindProcessor();
        unbindProcessor.add((condition, boss) -> {
            PatientXInstance inst = boss.getDetail().getZone();

            Location<World> target = inst.getCenter();
            for (Player player : inst.getPlayers(PlayerClassifier.PARTICIPANT)) {
                player.setLocation(target);

                boolean useX = Probability.getChance(2);
                int accel = Probability.getChance(2) ? 1 : -1;

                Vector3d v = new Vector3d(
                        useX ? accel : 0,
                        0,
                        !useX ? accel : 0
                );
                player.setVelocity(v);
            }

            inst.freezeBlocks(100, false);
            // Reset respawn mechanics
            inst.bossDied();

            return Optional.empty();
        });

        List<Instruction<DamageCondition, Boss<Zombie, ZoneBossDetail<PatientXInstance>>>> damageProcessor = bossManager.getDamageProcessor();
        damageProcessor.add((condition, boss) -> {
            PatientXInstance inst = boss.getDetail().getZone();
            DamageEntityEvent event = condition.getEvent();

            // Nullify all modifiers
            for (Tuple<DamageModifier, Function<? super Double, Double>> modifier : event.getModifiers()) {
                event.setDamage(modifier.getFirst(), (a) -> a);
            }

            event.setBaseDamage(inst.getDifficulty() * config.baseBossHit);
            return null;
        });


        List<Instruction<DamagedCondition, Boss<Zombie, ZoneBossDetail<PatientXInstance>>>> damagedProcessor = bossManager.getDamagedProcessor();
        damagedProcessor.add((condition, boss) -> {
            DamageEntityEvent event = condition.getEvent();
            Optional<DamageSource> optDamageSource = condition.getDamageSource();
            if (optDamageSource.isPresent() && blockedDamage.contains(optDamageSource.get().getType())) {
                event.setCancelled(true);
                return Optional.empty();
            }

            return Optional.of((Instruction<DamagedCondition, Boss<Zombie, ZoneBossDetail<PatientXInstance>>>) (damagedCondition, zombieZoneBossDetailBoss) -> {
                PatientXInstance inst = boss.getDetail().getZone();
                if (optDamageSource.isPresent() && optDamageSource.get() instanceof EntityDamageSource) {
                    if (optDamageSource.isPresent() && optDamageSource.get() instanceof IndirectEntityDamageSource) {
                        Location<World> curPos = inst.getBoss().get().getLocation();
                        Task.builder().execute(() -> {
                            VelocityEntitySpawner.sendRadial(EntityTypes.SNOWBALL, curPos, Cause.source(inst).build());
                        }).delayTicks(1).submit(SkreePlugin.inst());
                    } else {
                        Entity srcEntity = ((EntityDamageSource) optDamageSource.get()).getSource();

                        if (srcEntity instanceof Player) {
                            Optional<ItemStack> optHeld = ((Player) srcEntity).getItemInHand();
                            if (optHeld.isPresent() && optHeld.get().getItem() == ItemTypes.BLAZE_ROD) {
                                inst.modifyDifficulty(2);
                            }
                        }
                    }
                }

                inst.modifyDifficulty(.5);
                inst.teleportRandom(true);

                return Optional.empty();
            });
        });
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<Zone>> callback) {
        Function<Clause<ZoneRegion, ZoneRegion.State>, PatientXInstance> initFunc = clause -> {
            ZoneRegion region = clause.getKey();

            PatientXInstance instance = new PatientXInstance(region, config, bossManager);
            zones.add(instance);

            return instance;
        };

        Consumer<PatientXInstance> postInitFunc = instance -> {
            instance.init();

            callback.accept(Optional.of(instance));
        };

        allocator.regionFor(getSystemName(), initFunc, postInitFunc);
    }

    @Override
    public String getName() {
        return "Patient X";
    }

    @Override
    public void run() {
        Iterator<PatientXInstance> it = zones.iterator();
        while (it.hasNext()) {
            PatientXInstance next = it.next();
            if (next.isActive()) {
                next.run();
                continue;
            }
            next.forceEnd();
            it.remove();
        }
    }
}
