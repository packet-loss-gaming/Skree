/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour;

import com.skelril.nitro.Clause;
import com.skelril.openboss.BossListener;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneCreatureDropBlocker;
import com.skelril.skree.content.zone.ZoneInventoryProtector;
import com.skelril.skree.content.zone.group.freakyfour.boss.CharlotteBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.DaBombBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.FrimusBossManager;
import com.skelril.skree.content.zone.group.freakyfour.boss.SnipeeBossManager;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.CaveSpider;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

public class FreakyFourManager extends GroupZoneManager<FreakyFourInstance> implements Runnable, LocationZone<FreakyFourInstance> {
    private final Queue<ZoneRegion> freeRegions = new LinkedList<>();

    private final FreakyFourConfig config = new FreakyFourConfig();

    private final CharlotteBossManager charlotteManager = new CharlotteBossManager(config);
    private final FrimusBossManager frimusManager = new FrimusBossManager(config);
    private final DaBombBossManager daBombManager = new DaBombBossManager(config);
    private final SnipeeBossManager snipeeManager = new SnipeeBossManager(config);


    public FreakyFourManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new FreakyFourListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneInventoryProtector(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneCreatureDropBlocker(a -> getApplicableZone(a).isPresent())
        );

        registerManagerListeners();

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private void registerManagerListeners() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(charlotteManager, Living.class)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(charlotteManager.getMinionManager(), CaveSpider.class)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(frimusManager, Living.class)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(daBombManager, Living.class)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(snipeeManager, Living.class)
        );
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<Zone>> callback) {
        Consumer<Clause<ZoneRegion, ZoneRegion.State>> consumer = clause -> {
            ZoneRegion region = clause.getKey();

            FreakyFourInstance instance = new FreakyFourInstance(region, config, charlotteManager, frimusManager, daBombManager, snipeeManager);
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
        return "Freaky Four";
    }

    @Override
    public void run() {
        Iterator<FreakyFourInstance> it = zones.iterator();
        while (it.hasNext()) {
            FreakyFourInstance next = it.next();
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
