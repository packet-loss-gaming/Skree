/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import com.skelril.nitro.extractor.WorldFromExtent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class WorldEffectWrapperImpl implements WorldEffectWrapper {

    protected static WorldFromExtent toWorld = new WorldFromExtent();

    protected String name;
    protected Map<UUID, World> worlds;

    public WorldEffectWrapperImpl(String name) {
        this(name, new ArrayList<>());
    }

    public WorldEffectWrapperImpl(String name, Collection<World> worlds) {
        this.name = name;
        addWorld(worlds);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApplicable(Extent extent) {
        return worlds.containsKey(extent.getUniqueId());
    }

    @Override
    public void addWorld(World world) {
        worlds.put(world.getUniqueId(), world);
    }

    @Override
    public void addWorld(Collection<World> worlds) {
        worlds.forEach(this::addWorld);
    }

    @Override
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
