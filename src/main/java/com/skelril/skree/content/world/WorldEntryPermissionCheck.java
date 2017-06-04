/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;

import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class WorldEntryPermissionCheck {
  public static boolean checkDestination(Player player, World world) {
    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

    Optional<WorldEffectWrapper> optEffectWrapper = service.getEffectWrapperFor(world);
    String worldType = "misc";
    if (optEffectWrapper.isPresent()) {
      worldType = optEffectWrapper.get().getName();
    }

    return player.hasPermission("skree.world." + worldType.toLowerCase() + ".teleport");
  }
}
