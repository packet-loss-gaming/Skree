/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.instance;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class InstanceWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public InstanceWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public InstanceWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Instance", worlds);
        this.plugin = plugin;
        this.game = game;
    }
}