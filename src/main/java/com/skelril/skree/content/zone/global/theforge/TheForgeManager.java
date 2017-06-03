/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneImmutableBlockListener;
import com.skelril.skree.content.zone.ZoneTransitionalOrbListener;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.function.Consumer;

public class TheForgeManager extends GlobalZoneManager<TheForgeInstance> implements Runnable, LocationZone<TheForgeInstance> {

    public TheForgeManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new TheForgeListener(this)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneImmutableBlockListener<>(this::getApplicableZone)
        );
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ZoneTransitionalOrbListener<>(this::getApplicableZone)
        );

        Task.builder().intervalTicks(10).execute(this).submit(SkreePlugin.inst());
    }

    @Override
    public void init(ZoneSpaceAllocator allocator, Consumer<TheForgeInstance> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            TheForgeInstance instance = new TheForgeInstance(region);
            instance.init();

            callback.accept(instance);
        });
    }

    @Override
    public String getName() {
        return "The Forge";
    }

    @Override
    public void run() {
        Optional<TheForgeInstance> optInstance = getActiveZone();
        if (!optInstance.isPresent()) {
            return;
        }

        TheForgeInstance inst = optInstance.get();
        inst.run();
    }
}