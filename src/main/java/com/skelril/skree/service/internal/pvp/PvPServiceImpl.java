/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.pvp;

import com.skelril.skree.service.PvPService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.HashMap;
import java.util.UUID;

public class PvPServiceImpl implements PvPService {

    private HashMap<UUID, PvPState> stateMap = new HashMap<>();

    @Listener
    public void onDisconnect(ClientConnectionEvent.Disconnect event) {
        stateMap.remove(event.getTargetEntity().getUniqueId());
    }

    @Override
    public void setPvPState(Player player, PvPState state) {
        stateMap.put(player.getUniqueId(), state);
    }

    @Override
    public PvPState getPvPState(Player player) {
        return stateMap.getOrDefault(player.getUniqueId(), getDefaultState(player));
    }

    @Override
    public PvPState getDefaultState(Player player) {
        return PvPState.DENIED;
    }
}
