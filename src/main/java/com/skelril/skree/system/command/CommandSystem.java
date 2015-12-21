/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.command;


import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.CommandService;
import com.skelril.skree.service.internal.command.CommandServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;

public class CommandSystem implements ServiceProvider<CommandService> {

    private CommandService service;

    public CommandSystem(SkreePlugin plugin, Game game) {
        service = new CommandServiceImpl();
        try {
            game.getServiceManager().setProvider(plugin, CommandService.class, service);
        } catch (ProviderExistsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandService getService() {
        return service;
    }
}