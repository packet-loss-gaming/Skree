/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.ZoneManager;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.entity.player.Player;

import java.util.Collection;

public interface ZoneService {
    void registerManager(ZoneManager<?> manager);

    Clause<Player, ZoneStatus> requestZone(String managerName, Player player);
    Collection<Clause<Player, ZoneStatus>> requestZone(String managerName, Collection<Player> players);

    Clause<Player, ZoneStatus> requestZone(ZoneManager<?> manager, Player player);
    Collection<Clause<Player, ZoneStatus>> requestZone(ZoneManager<?> manager, Collection<Player> players);

    Clause<Player, ZoneStatus> rejoin(Player player);
    Collection<Clause<Player, ZoneStatus>> rejoin(Collection<Player> players);
}
