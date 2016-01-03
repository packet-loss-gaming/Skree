/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.shutdown;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.shutdown.ShutdownCommand;
import com.skelril.skree.service.ShutdownService;
import com.skelril.skree.service.internal.shutdown.ShutdownServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Shutdown System")
public class ShutdownSystem implements ServiceProvider<ShutdownService> {

    private ShutdownService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new ShutdownServiceImpl();

        // Register the service & command
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ShutdownService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), ShutdownCommand.aquireSpec(), "shutdown");
    }

    @Override
    public ShutdownService getService() {
        return service;
    }
}
