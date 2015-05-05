/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.inject.Inject;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.logging.Logger;

@Plugin(id = "Skree", name = "Skree", version = "1.0")
public class SkreePlugin {

    @Inject
    private Logger logger;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        logger.info("Skree Started! Kaw!");
    }
}
