/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.dropclear;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.dropclear.DropClearCommand;
import com.skelril.skree.service.DropClearService;
import com.skelril.skree.service.internal.dropclear.DropClearServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Drop Clear System")
public class DropClearSystem implements ServiceProvider<DropClearService> {

    private DropClearService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new DropClearServiceImpl(1000, 3);

        // Register the service & command
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), DropClearService.class, service);
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);

        Sponge.getCommandManager().register(SkreePlugin.inst(), DropClearCommand.aquireSpec(120), "dropclear", "dc");
    }

    @Override
    public DropClearService getService() {
        return service;
    }
}
