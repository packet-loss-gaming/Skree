/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine;

import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneNaturalSpawnBlocker;
import com.skelril.skree.content.zone.global.cursedmine.hitlist.HitList;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CursedMineManager extends GlobalZoneManager<CursedMineInstance> implements Runnable, LocationZone<CursedMineInstance> {
    private HitList hitList = new HitList();

    public CursedMineManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new CursedMineListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneNaturalSpawnBlocker<>(this::getApplicableZone)
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    public HitList getHitList() {
        return hitList;
    }

    @Override
    public void init(ZoneSpaceAllocator allocator, Consumer<CursedMineInstance> callback) {
        Function<Clause<ZoneRegion, ZoneRegion.State>, CursedMineInstance> initFunc = clause -> {
            ZoneRegion region = clause.getKey();

            return new CursedMineInstance(region, hitList);
        };

        Consumer<CursedMineInstance> postInitFunc = instance -> {
            instance.init();

            callback.accept(instance);
        };

        allocator.regionFor(getSystemName(), initFunc, postInitFunc);
    }

    @Override
    public String getName() {
        return "Cursed Mine";
    }

    @Override
    public void run() {
        Optional<CursedMineInstance> optInst = getActiveZone();
        if (optInst.isPresent()) {
            CursedMineInstance inst = optInst.get();
            if (inst.isActive()) {
                inst.run();
                return;
            }
            zone.forceEnd();

            Optional<ZoneSpaceAllocator> optAllocator = zone.getRegion().getAllocator();
            if (optAllocator.isPresent()) {
                optAllocator.get().release(getSystemName(), zone.getRegion());
            }

            zone = null;
        }
    }
}
