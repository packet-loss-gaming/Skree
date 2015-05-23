/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.skelril.skree.system.dropclear.DropClearSystem;
import com.skelril.skree.system.modifier.ModifierSystem;
import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import com.skelril.skree.system.shutdown.ShutdownSystem;
import com.skelril.skree.system.world.WorldSystem;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.logging.Logger;

@Singleton
@Plugin(id = "Skree", name = "Skree", version = "1.0")
public class SkreePlugin {

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Subscribe
    public void onPreInit(PreInitializationEvent event) {
        new CustomItemSystem(this, game).init();
        new CustomBlockSystem(this, game).init();
        logger.info("Skree registry modifications complete!");
    }

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        registerPrimaryHybridSystems();
        switch (game.getPlatform().getType()) {
            case CLIENT:
                registerPrimaryClientSystems();
                break;
            case SERVER:
                registerPrimaryServerSystems();
                break;
        }

        logger.info("Skree Started! Kaw!");
    }

    private void registerPrimaryHybridSystems() {

    }

    private void registerPrimaryClientSystems() {

    }

    private void registerPrimaryServerSystems() {
        try {
            new DropClearSystem(this, game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            new ModifierSystem(this, game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            new ShutdownSystem(this, game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            new WorldSystem(this, game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
