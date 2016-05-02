/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.jungleraid;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneNaturalSpawnBlocker;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.decorator.Decorators;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

public class JungleRaidManager extends GroupZoneManager<JungleRaidInstance> implements Runnable, LocationZone<JungleRaidInstance> {
    private Queue<Vector3i> previousOrigins = new ArrayDeque<>();
    private Clause<ZoneRegion, ZoneRegion.State> preBuilt = null;
    private boolean isBuilding = false;

    public JungleRaidManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new JungleRaidListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new JungleRaidEffectListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneNaturalSpawnBlocker(a -> getApplicableZone(a).isPresent())
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private void buildInstance(WorldResolver resolver, Vector3i origin, Consumer<Clause<ZoneRegion, ZoneRegion.State>> clause) {
        Decorators.ZONE_PRIMARY_DECORATOR.pasteAt(
                resolver,
                origin,
                getSystemName(),
                (a) -> new Clause<>(a.getKey(), ZoneRegion.State.NEW),
                clause
        );
    }

    @Override
    public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<JungleRaidInstance>> callback) {
        Consumer<Clause<ZoneRegion, ZoneRegion.State>> initializer = clause -> {
            ZoneRegion region = clause.getKey();

            JungleRaidInstance instance = new JungleRaidInstance(region);
            instance.init();
            zones.add(instance);

            callback.accept(Optional.of(instance));

            if (preBuilt == null && !isBuilding) {
                isBuilding = true;
                Vector3i origin = previousOrigins.poll();
                if (origin != null) {
                    buildInstance(allocator.getWorldResolver(), origin, (preBuildClause) -> {
                        preBuilt = preBuildClause;
                        isBuilding = false;
                    });
                } else {
                    allocator.regionFor(getSystemName(), (preBuildClause) -> {
                        preBuilt = preBuildClause;
                        isBuilding = false;
                    });
                }
            }
        };

        if (preBuilt != null) {
            Clause<ZoneRegion, ZoneRegion.State> tempPreBuilt = preBuilt;
            preBuilt = null;
            initializer.accept(tempPreBuilt);
        } else {
            Vector3i origin = previousOrigins.poll();
            if (origin != null) {
                buildInstance(allocator.getWorldResolver(), origin, initializer);
            } else {
                allocator.regionFor(getSystemName(), initializer);
            }
        }
    }

    @Override
    public String getName() {
        return "Jungle Raid";
    }

    @Override
    public void run() {
        Iterator<JungleRaidInstance> it = zones.iterator();
        while (it.hasNext()) {
            JungleRaidInstance next = it.next();
            if (next.isActive()) {
                next.run();
                continue;
            }
            next.forceEnd();
            it.remove();
            next.getRegion().getMinimumPoint();
        }
    }

}