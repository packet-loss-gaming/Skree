/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public class ItemDropper {

    private final Location<World> location;

    public ItemDropper(Location<World> location) {
        this.location = location;
    }

    public World getExtent() {
        return location.getExtent();
    }

    public Vector3d getPos() {
        return location.getPosition();
    }

    public void dropItems(Collection<ItemStack> stacks, SpawnType type) {
        for (ItemStack stack : stacks) {
            Optional<Entity> optEntity = getExtent().createEntity(EntityTypes.ITEM, getPos());
            if (optEntity.isPresent()) {
                Item item = (Item) optEntity.get();
                item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());
                getExtent().spawnEntity(item, Cause.source(SpawnCause.builder().type(type).build()).build());
            }
        }
    }
}
