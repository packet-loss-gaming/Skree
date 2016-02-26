/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.sk89q.worldedit.WorldEdit;
import org.spongepowered.api.world.World;

public class WorldResolver {

    private World spongeWorld;
    private com.sk89q.worldedit.world.World worldEditWorld;

    public WorldResolver(World world) {
        this(world, WorldEdit.getInstance());
    }

    public WorldResolver(World world, WorldEdit worldEdit) {
        this.spongeWorld = world;
        for (com.sk89q.worldedit.world.World aWorld : worldEdit.getServer().getWorlds()) {
            if (aWorld.getName().equals(world.getName())) {
                this.worldEditWorld = aWorld;
                break;
            }
        }
    }

    public World getSpongeWorld() {
        return spongeWorld;
    }

    public com.sk89q.worldedit.world.World getWorldEditWorld() {
        return worldEditWorld;
    }
}
