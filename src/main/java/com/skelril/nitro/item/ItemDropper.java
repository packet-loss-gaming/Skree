/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public class ItemDropper {

    private Game game;
    private World world;
    private Vector3d pos;

    public ItemDropper(Game game, World world, Vector3d pos) {
        this.game = game;
        this.world = world;
        this.pos = pos;
    }

    public void dropItems(Collection<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            Optional<Entity> optEntity = world.createEntity(EntityTypes.ITEM, pos);
            if (optEntity.isPresent()) {
                Item item = (Item) optEntity.get();
                item.offer(item.getItemData().set(Keys.REPRESENTED_ITEM, stack.createSnapshot()));
                // item.offer(item.getData(VelocityData.class).get().setValue(new Vector3d(random.nextFloat() % 1, random.nextFloat() % 1, random.nextFloat() % 1)));
                world.spawnEntity(item, Cause.empty());
            }
        }
    }
}
