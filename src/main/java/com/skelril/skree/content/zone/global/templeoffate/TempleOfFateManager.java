/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.templeoffate;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.*;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.function.Consumer;

public class TempleOfFateManager extends GlobalZoneManager<TempleOfFateInstance> implements Runnable, LocationZone<TempleOfFateInstance> {

  public TempleOfFateManager() {
    Sponge.getEventManager().registerListeners(
        SkreePlugin.inst(),
        new TempleOfFateListener(this)
    );
    Sponge.getEventManager().registerListeners(
        SkreePlugin.inst(),
        new ZoneImmutableBlockListener<>(this::getApplicableZone)
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
        new ZoneTransitionalOrbListener<>(this::getApplicableZone)
    );

    Task.builder().intervalTicks(10).execute(this).submit(SkreePlugin.inst());
  }

  @Override
  public void init(ZoneSpaceAllocator allocator, Consumer<TempleOfFateInstance> callback) {
    allocator.regionFor(getSystemName(), clause -> {
      ZoneRegion region = clause.getKey();

      TempleOfFateInstance instance = new TempleOfFateInstance(region);
      instance.init();

      callback.accept(instance);
    });
  }

  @Override
  public String getName() {
    return "Temple of Fate";
  }

  @Override
  public void run() {
    Optional<TempleOfFateInstance> optInstance = getActiveZone();
    if (!optInstance.isPresent()) {
      return;
    }

    TempleOfFateInstance inst = optInstance.get();
    inst.run();
  }
}