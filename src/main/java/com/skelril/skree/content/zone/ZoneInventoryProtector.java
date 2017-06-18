/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.skree.service.PlayerStateService;
import com.skelril.skree.service.internal.playerstate.InventoryStorageStateException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneInventoryProtector<T> extends ZoneApplicableListener<T> {
  public ZoneInventoryProtector(Function<Location<World>, Optional<T>> applicabilityFunct) {
    super(applicabilityFunct);
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player) {
    if (!isApplicable(player)) {
      return;
    }

    PlayerStateService service = Sponge.getServiceManager().provideUnchecked(PlayerStateService.class);
    try {
      service.storeInventory(player);
      service.releaseInventory(player);

      player.getInventory().clear();
    } catch (InventoryStorageStateException e) {
      e.printStackTrace();
    }
  }
}
