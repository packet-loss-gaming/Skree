/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.respawn;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.RespawnService;
import com.skelril.skree.service.internal.respawn.RespawnServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Respawn System")
public class RespawnSystem implements ServiceProvider<RespawnService> {
    private RespawnService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new RespawnServiceImpl();

        // Register the service
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), RespawnService.class, service);
    }

    @Override
    public RespawnService getService() {
        return service;
    }
}
