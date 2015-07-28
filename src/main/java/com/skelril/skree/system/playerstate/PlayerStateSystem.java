/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.playerstate;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.playerstate.GameModeCommand;
import com.skelril.skree.service.PlayerStateService;
import com.skelril.skree.service.internal.playerstate.PlayerStateServiceImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.command.CommandService;

public class PlayerStateSystem {

    private PlayerStateService service;

    public PlayerStateSystem(SkreePlugin plugin, Game game) {

        service = new PlayerStateServiceImpl();

        // Register the service & command
        try {
            game.getServiceManager().setProvider(plugin, PlayerStateService.class, service);

            CommandService cmdDispatcher = game.getCommandDispatcher();

            cmdDispatcher.removeMapping(cmdDispatcher.get("gamemode").get());
            cmdDispatcher.register(plugin, GameModeCommand.aquireSpec(game, service), "gamemode", "gm");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    public PlayerStateService getService() {
        return service;
    }
}
