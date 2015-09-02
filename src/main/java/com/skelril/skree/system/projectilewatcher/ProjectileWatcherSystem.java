/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.projectilewatcher;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ProjectileWatcherService;
import com.skelril.skree.service.internal.projectilewatcher.ProjectileWatcherServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.ProviderExistsException;

public class ProjectileWatcherSystem implements ServiceProvider<ProjectileWatcherService> {
    private ProjectileWatcherService service;

    public ProjectileWatcherSystem(SkreePlugin plugin, Game game) {

        service = new ProjectileWatcherServiceImpl(plugin, game);

        // Register the service & command
        try {
            game.getEventManager().registerListeners(plugin, service);
            game.getServiceManager().setProvider(plugin, ProjectileWatcherService.class, service);
        } catch (ProviderExistsException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public ProjectileWatcherService getService() {
        return service;
    }
}
