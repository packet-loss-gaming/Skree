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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderExistsException;

public class DropClearSystem implements ServiceProvider<DropClearService> {

    private DropClearService service;

    public DropClearSystem() {
        service = new DropClearServiceImpl(1000, 3);

        // Register the service & command
        try {
            Sponge.getServiceManager().setProvider(SkreePlugin.inst(), DropClearService.class, service);
            Sponge.getCommandManager().register(SkreePlugin.inst(), DropClearCommand.aquireSpec(120), "dropclear", "dc");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }

        Task.builder().execute(
                () -> Sponge.getServer().getWorlds().stream().forEach(service::checkedCleanup)
        ).intervalTicks(10).submit(SkreePlugin.inst());
    }

    @Override
    public DropClearService getService() {
        return service;
    }
}
