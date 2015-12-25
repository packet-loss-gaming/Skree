/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneManager;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface ZoneService {
    static String mangleManagerName(String managerName) {
        return managerName.toLowerCase().replace(" ", "");
    }

    void registerManager(ZoneManager<?> manager);

    Optional<Integer> getMaxGroupSize(String managerName);
    Clause<Player, ZoneStatus> requestZone(String managerName, Player player);
    Collection<Clause<Player, ZoneStatus>> requestZone(String managerName, Collection<Player> players);

    <T extends Zone>Optional<Integer> getMaxGroupSize(ZoneManager<T> manager);
    <T extends Zone> Clause<Player, ZoneStatus> requestZone(ZoneManager<T> manager, Player player);
    <T extends Zone> Collection<Clause<Player, ZoneStatus>> requestZone(ZoneManager<T> manager, Collection<Player> players);

    Clause<Player, ZoneStatus> rejoin(Player player);
    Collection<Clause<Player, ZoneStatus>> rejoin(Collection<Player> players);
}
