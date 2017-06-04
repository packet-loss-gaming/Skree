/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class FixedPointItemDropper extends ItemDropper {
    public FixedPointItemDropper(Location<World> location) {
        super(location);
    }

    @Override
    public void dropItem(ItemStackSnapshot snapshot, Cause cause) {
        Item item = (Item)  getExtent().createEntity(EntityTypes.ITEM, getPos());
        item.offer(Keys.REPRESENTED_ITEM, snapshot);
        item.setVelocity(new Vector3d(0, 0, 0));
        getExtent().spawnEntity(item, cause);
    }

}
