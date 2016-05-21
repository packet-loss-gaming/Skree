/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.goldrush;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneNaturalSpawnBlocker;
import com.skelril.skree.content.zone.ZonePvPListener;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class GoldRushManager extends GroupZoneManager<GoldRushInstance> implements Runnable, LocationZone<GoldRushInstance> {
    public GoldRushManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new GoldRushListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneNaturalSpawnBlocker(a -> getApplicableZone(a).isPresent())
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZonePvPListener(a -> getApplicableZone(a).isPresent())
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<GoldRushInstance>> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            GoldRushInstance instance = new GoldRushInstance(region);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));
        });
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

            Optional<ZoneSpaceAllocator> optAllocator = next.getRegion().getAllocator();
            if (optAllocator.isPresent()) {
                optAllocator.get().release(getSystemName(), next.getRegion());
            }

            it.remove();
        }
    }

}
