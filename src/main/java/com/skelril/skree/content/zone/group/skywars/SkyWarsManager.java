/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.skywars;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LocationZone;
import com.skelril.skree.content.zone.ZoneImmutableBlockListener;
import com.skelril.skree.content.zone.ZoneNaturalSpawnBlocker;
import com.skelril.skree.content.zone.ZoneTransitionalOrbListener;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class SkyWarsManager extends GroupZoneManager<SkyWarsInstance> implements Runnable, LocationZone<SkyWarsInstance> {
  public SkyWarsManager() {
    Sponge.getEventManager().registerListeners(
        SkreePlugin.inst(),
        new SkyWarsListener(this)
    );
    Sponge.getEventManager().registerListeners(
        SkreePlugin.inst(),
        new ZoneNaturalSpawnBlocker<>(this::getApplicableZone)
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
  public void discover(ZoneSpaceAllocator allocator, Consumer<Optional<SkyWarsInstance>> callback) {
    allocator.regionFor(getSystemName(), clause -> {
      ZoneRegion region = clause.getKey();

      SkyWarsInstance instance = new SkyWarsInstance(region);
      instance.init();
      zones.add(instance);

      callback.accept(Optional.of(instance));
    });
  }

  @Override
  public String getName() {
    return "Sky Wars";
  }

  @Override
  public void run() {
    Iterator<SkyWarsInstance> it = zones.iterator();
    while (it.hasNext()) {
      SkyWarsInstance next = it.next();
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

