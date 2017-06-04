/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.desmiredungeon;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.*;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class DesmireDungeonManager extends GroupZoneManager<DesmireDungeonInstance> implements Runnable, LocationZone<DesmireDungeonInstance> {
    public DesmireDungeonManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new DesmireDungeonListener(this)
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
                new ZoneGlobalHealthPrinter<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneTransitionalOrbListener<>(this::getApplicableZone)
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<DesmireDungeonInstance>> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            DesmireDungeonInstance instance = new DesmireDungeonInstance(region);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));
        });
    }

    @Override
    public String getName() {
        return "Desmire Dungeon";
    }

    @Override
    public void run() {
        Iterator<DesmireDungeonInstance> it = zones.iterator();
        while (it.hasNext()) {
            DesmireDungeonInstance next = it.next();
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
