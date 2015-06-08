/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.main;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class MainWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public MainWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public MainWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Main", worlds);
        this.plugin = plugin;
        this.game = game;
    }

    @Subscribe
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!isApplicable(event.getLocation().getExtent())) return;

        // TODO Smarter "should this mob be allowed to spawn" code
        if (event.getEntity() instanceof Monster) {
            event.setCancelled(true);
        }
    }
}
