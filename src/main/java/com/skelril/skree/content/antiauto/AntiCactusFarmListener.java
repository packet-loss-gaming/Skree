/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.antiauto;

import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCause;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class AntiCactusFarmListener {
  @Listener
  public void onItemDrop(DropItemEvent.Destruct event, @Named(NamedCause.SOURCE) BlockSpawnCause spawnCause) {
    if (event.getCause().containsType(Player.class)) {
      return;
    }

    BlockSnapshot blockSnapshot = spawnCause.getBlockSnapshot();

    Optional<Location<World>> optLocation = blockSnapshot.getLocation();
    if (!optLocation.isPresent()) {
      return;
    }

    Location<World> location = optLocation.get();
    while (true) {
      location = location.add(0, -1, 0);
      if (location.getBlockType() != BlockTypes.CACTUS) {
        break;
      }
      location.setBlockType(BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());
    }
  }
}
