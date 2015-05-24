/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.block;

import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.Game;

public class CustomBlockSystem {

    private final SkreePlugin plugin;
    private final Game game;

    public CustomBlockSystem(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void preInit() {

    }

    public void init() {

    }
}
