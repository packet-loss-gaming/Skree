/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import com.skelril.skree.service.api.world.WorldService;
import com.skelril.skree.service.api.world.WorldEffectWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class WorldServiceImpl implements WorldService {

    private HashMap<String, WorldEffectWrapper> worlds = new HashMap<>();

    @Override
    public void registerEffectWrapper(WorldEffectWrapper world) {
        worlds.put(world.getName(), world);
    }

    @Override
    public WorldEffectWrapper getEffectWrapper(String name) {
        return worlds.get(name);
    }

    @Override
    public Collection<WorldEffectWrapper> getEffectWrappers() {
        return new HashSet<>(worlds.values());
    }
}
