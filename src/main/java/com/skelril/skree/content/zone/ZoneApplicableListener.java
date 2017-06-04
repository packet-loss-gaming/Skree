/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneApplicableListener<T> {
  private final Function<Location<World>, Optional<T>> applicabilityFunct;

  public ZoneApplicableListener(Function<Location<World>, Optional<T>> applicabilityFunct) {
    this.applicabilityFunct = applicabilityFunct;
  }

  public boolean isApplicable(Entity entity) {
    return isApplicable(entity.getLocation());
  }

  public boolean isApplicable(Location<World> location) {
    return getApplicable(location).isPresent();
  }

  public Optional<T> getApplicable(Entity entity) {
    return getApplicable(entity.getLocation());
  }

  public Optional<T> getApplicable(Location<World> location) {
    return applicabilityFunct.apply(location);
  }
}
