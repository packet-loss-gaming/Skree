/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.projectilewatcher;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ProjectileWatcherService;
import com.skelril.skree.service.internal.projectilewatcher.ProjectileWatcherServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Projectile Watcher System")
public class ProjectileWatcherSystem implements ServiceProvider<ProjectileWatcherService> {
    private ProjectileWatcherService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new ProjectileWatcherServiceImpl();

        // Register the service & command
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ProjectileWatcherService.class, service);
    }

    @Override
    public ProjectileWatcherService getService() {
        return service;
    }
}
