/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs;

import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.*;
import com.skelril.skree.content.zone.group.catacombs.instruction.CatacombsHealthInstruction;
import com.skelril.skree.content.zone.group.catacombs.instruction.CheckedSpawnWave;
import com.skelril.skree.content.zone.group.catacombs.instruction.WaveDamageModifier;
import com.skelril.skree.content.zone.group.catacombs.instruction.bossmove.NamedBindInstruction;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CatacombsManager extends GroupZoneManager<CatacombsInstance> implements Runnable, LocationZone<CatacombsInstance> {
    private final BossManager<Zombie, CatacombsBossDetail> bossManager = new BossManager<>();
    private final BossManager<Zombie, CatacombsBossDetail> waveMobManager = new BossManager<>();

    public CatacombsManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new CatacombsListener(this)
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
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneGlobalHealthPrinter(a -> getApplicableZone(a).isPresent())
        );

        setUpBoss();
        setUpWave();
        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private void setUpBoss() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(bossManager, Zombie.class)
        );


        List<Instruction<BindCondition, Boss<Zombie, CatacombsBossDetail>>> bindProcessor = bossManager.getBindProcessor();
        bindProcessor.add(new NamedBindInstruction<>("Necromancer"));
        bindProcessor.add(new CatacombsHealthInstruction(250));

        List<Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>>> unbindProcessor = bossManager.getUnbindProcessor();
        unbindProcessor.add(new CheckedSpawnWave());

        List<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> damageProcessor = bossManager.getDamageProcessor();
        damageProcessor.add(new WaveDamageModifier());
    }

    private void setUpWave() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(waveMobManager, Zombie.class)
        );

        List<Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>>> unbindProcessor = waveMobManager.getUnbindProcessor();
        unbindProcessor.add(new CheckedSpawnWave());

        List<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> damageProcessor = waveMobManager.getDamageProcessor();
        damageProcessor.add(new WaveDamageModifier());
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<CatacombsInstance>> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            CatacombsInstance instance = new CatacombsInstance(region, bossManager, waveMobManager);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));
        });
    }

    @Override
    public String getName() {
        return "Catacombs";
    }

    @Override
    public void run() {
        Iterator<CatacombsInstance> it = zones.iterator();
        while (it.hasNext()) {
            CatacombsInstance next = it.next();
            if (next.isActive()) {
                next.run();
                continue;
            }
            next.forceEnd();
            it.remove();
        }
    }
}
