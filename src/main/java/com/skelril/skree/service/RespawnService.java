/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public interface RespawnService {
    Location<World> getDefault(Player target);

    void push(Player player, Location<World> target);

    Optional<Location<World>> peek(Player player);
    Optional<Location<World>> pop(Player player);
}
