/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.shutdown;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.shutdown.ShutdownCommand;
import com.skelril.skree.service.ShutdownService;
import com.skelril.skree.service.internal.shutdown.ShutdownServiceImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;

public class ShutdownSystem {

    private ShutdownService service;

    @Inject
    public ShutdownSystem(SkreePlugin plugin, Game game) {

        service = new ShutdownServiceImpl(plugin, game);

        // Register the service & command
        try {
            game.getServiceManager().setProvider(plugin, ShutdownService.class, service);
            game.getCommandDispatcher().register(plugin, ShutdownCommand.aquireSpec(service), "shutdown");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    public ShutdownService getService() {
        return service;
    }
}
