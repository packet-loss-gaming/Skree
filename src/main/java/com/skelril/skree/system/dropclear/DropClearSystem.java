/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.dropclear;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.dropclear.DropClearCommand;
import com.skelril.skree.service.DropClearService;
import com.skelril.skree.service.internal.dropclear.DropClearServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;

public class DropClearSystem implements ServiceProvider<DropClearService> {

    private DropClearService service;

    public DropClearSystem(SkreePlugin plugin, Game game) {
        service = new DropClearServiceImpl(plugin, game, 1000, 3);

        // Register the service & command
        try {
            game.getServiceManager().setProvider(plugin, DropClearService.class, service);
            game.getCommandManager().register(plugin, DropClearCommand.aquireSpec(game, service, 120), "dropclear", "dc");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }

        game.getScheduler().createTaskBuilder().execute(
                () -> game.getServer().getWorlds().stream().forEach(service::checkedCleanup)
        ).intervalTicks(10).submit(plugin);
    }

    @Override
    public DropClearService getService() {
        return service;
    }
}
