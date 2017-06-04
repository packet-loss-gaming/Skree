/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public interface LocationZone<T extends LegacyZoneBase> {
  default Optional<T> getApplicableZone(BlockSnapshot block) {
    return getApplicableZone(block.getLocation().get());
  }

  default Optional<T> getApplicableZone(Entity entity) {
    return getApplicableZone(entity.getLocation());
  }

  default Optional<T> getApplicableZone(Location<World> loc) {
    for (T inst : getActiveZones()) {
      if (inst.contains(loc)) {
        return Optional.of(inst);
      }
    }
    return Optional.empty();
  }

  Collection<T> getActiveZones();
}
