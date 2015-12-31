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
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderExistsException;

public class PlayerStateSystem implements ServiceProvider<PlayerStateService> {

    private PlayerStateService service;

    public PlayerStateSystem() {

        service = new PlayerStateServiceImpl();

        // Register the service & command
        try {
            Sponge.getServiceManager().setProvider(SkreePlugin.inst(), PlayerStateService.class, service);

            Sponge.getCommandManager().removeMapping(Sponge.getCommandManager().get("gamemode").get());
            Sponge.getCommandManager().register(SkreePlugin.inst(), GameModeCommand.aquireSpec(), "gamemode", "gm");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public PlayerStateService getService() {
        return service;
    }
}
