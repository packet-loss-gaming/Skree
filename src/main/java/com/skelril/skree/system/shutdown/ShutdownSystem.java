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
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderExistsException;

public class ShutdownSystem implements ServiceProvider<ShutdownService> {

    private ShutdownService service;

    @Inject
    public ShutdownSystem() {

        service = new ShutdownServiceImpl();

        // Register the service & command
        try {
            Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ShutdownService.class, service);
            Sponge.getCommandManager().register(SkreePlugin.inst(), ShutdownCommand.aquireSpec(), "shutdown");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public ShutdownService getService() {
        return service;
    }
}
