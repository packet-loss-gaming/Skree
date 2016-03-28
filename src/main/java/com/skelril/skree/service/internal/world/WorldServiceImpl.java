/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import com.skelril.skree.service.WorldService;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class WorldServiceImpl implements WorldService {

    private HashMap<Class<? extends WorldEffectWrapper>, WorldEffectWrapper> worlds = new HashMap<>();

    @Override
    public void registerEffectWrapper(WorldEffectWrapper wrapper) {
        worlds.put(wrapper.getClass(), wrapper);
    }

    @Override
    public <T extends WorldEffectWrapper> Optional<T> getEffectWrapper(Class<T> wrapperClass) {
        WorldEffectWrapper wrapper = worlds.get(wrapperClass);
        //noinspection unchecked
        return Optional.ofNullable(wrapper != null ? (T) wrapper : null);
    }

    @Override
    public Optional<WorldEffectWrapper> getEffectWrapperFor(World world) {
        for (WorldEffectWrapper wrapper : worlds.values()) {
            for (World worldEntry : wrapper.getWorlds()) {
                if (world.equals(worldEntry)) {
                    return Optional.of(wrapper);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<WorldEffectWrapper> getEffectWrappers() {
        return new HashSet<>(worlds.values());
    }
}
