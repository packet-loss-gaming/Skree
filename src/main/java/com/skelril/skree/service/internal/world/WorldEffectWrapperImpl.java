/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class WorldEffectWrapperImpl implements WorldEffectWrapper {

    protected String name;
    protected Collection<World> worlds;

    public WorldEffectWrapperImpl(String name) {
        this(name, new ArrayList<>());
    }

    public WorldEffectWrapperImpl(String name, Collection<World> worlds) {
        this.name = name;
        this.worlds = new ArrayList<>(worlds);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApplicable(World world) {
        return worlds.contains(world);
    }

    @Override
    public void addWorld(World world) {
        worlds.add(world);
    }

    @Override
    public Collection<World> getWorlds() {
        return worlds;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
