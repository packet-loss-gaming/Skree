/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.system.world;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.api.world.WorldService;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import org.spongepowered.api.Game;

public class WorldSystem {

    private WorldService service;

    @Inject
    public WorldSystem(SkreePlugin plugin, Game game) {
        service = new WorldServiceImpl();
        WildernessWorldWrapper wrapper = new WildernessWorldWrapper(plugin, game);
        game.getServer().getWorlds().forEach(wrapper::addWorld);
        game.getEventManager().register(plugin, wrapper);
        service.registerEffectWrapper(wrapper);
    }
}
