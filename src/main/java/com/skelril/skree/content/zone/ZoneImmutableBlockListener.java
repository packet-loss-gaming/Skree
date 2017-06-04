/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneImmutableBlockListener<T> extends ZoneApplicableListener<T> {
  public ZoneImmutableBlockListener(Function<Location<World>, Optional<T>> applicabilityFunct) {
    super(applicabilityFunct);
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent event, @First Player player) {
    if (isApplicable(player)) {
      event.setCancelled(true);
    }
  }
}
