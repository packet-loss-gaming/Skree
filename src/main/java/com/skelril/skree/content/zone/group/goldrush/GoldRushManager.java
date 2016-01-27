/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.goldrush;

import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class GoldRushManager extends GroupZoneManager<GoldRushInstance> implements Runnable {
    private Queue<ZoneRegion> freeRegions = new LinkedList<>();

    public GoldRushManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new GoldRushListener(this)
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    public Optional<GoldRushInstance> getApplicableZone(Entity entity) {
        for (GoldRushInstance inst : zones) {
            if (inst.contains(entity)) {
                return Optional.of(inst);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<GoldRushInstance> discover(ZoneSpaceAllocator allocator) {
        ZoneRegion region = freeRegions.poll();
        if (region == null) {
            Clause<ZoneRegion, ZoneRegion.State> result = allocator.regionFor(getSystemName());
            region = result.getKey();
        }

        GoldRushInstance instance = new GoldRushInstance(region);
        instance.init();

        zones.add(instance);

        return Optional.of(instance);
    }

    @Override
    public String getName() {
        return "Gold Rush";
    }

    @Override
    public void run() {
        Iterator<GoldRushInstance> it = zones.iterator();
        while (it.hasNext()) {
            GoldRushInstance next = it.next();
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
