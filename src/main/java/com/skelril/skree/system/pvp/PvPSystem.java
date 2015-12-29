/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.pvp;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.pvp.PvPCommand;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.pvp.PvPServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.service.ProviderExistsException;

public class PvPSystem implements ServiceProvider<PvPService> {

    private PvPService service;

    public PvPSystem(SkreePlugin plugin, Game game) {
        CommandManager cmdDispatcher = game.getCommandManager();

        service = new PvPServiceImpl();

        // Register the service & command
        try {
            game.getEventManager().registerListeners(plugin, service);
            game.getServiceManager().setProvider(plugin, PvPService.class, service);
            cmdDispatcher.register(plugin, PvPCommand.aquireSpec(), "pvp");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public PvPService getService() {
        return service;
    }
}
