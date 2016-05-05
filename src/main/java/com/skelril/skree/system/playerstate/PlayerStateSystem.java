/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.playerstate;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.playerstate.GameModeCommand;
import com.skelril.skree.service.PlayerStateService;
import com.skelril.skree.service.internal.playerstate.PlayerStateServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Player State System")
public class PlayerStateSystem implements ServiceProvider<PlayerStateService> {

    private PlayerStateService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new PlayerStateServiceImpl();

        // Register the service & command
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), PlayerStateService.class, service);
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);

        Sponge.getCommandManager().removeMapping(Sponge.getCommandManager().get("gamemode").get());
        Sponge.getCommandManager().register(SkreePlugin.inst(), GameModeCommand.aquireSpec(), "gamemode", "gm");
    }

    @Override
    public PlayerStateService getService() {
        return service;
    }
}
