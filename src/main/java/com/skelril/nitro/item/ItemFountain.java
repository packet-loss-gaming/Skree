/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.skelril.nitro.generator.Generator;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.world.World;

import java.util.Collection;

public class ItemFountain implements IntegratedRunnable  {
    private Game game;
    private World world;
    private Vector3d pos;
    private Generator<Integer> amplifier;
    private Collection<ItemStack> options;

    public ItemFountain(Game game, World world, Vector3d pos, Generator<Integer> amplifier, Collection<ItemStack> options) {
        this.game = game;
        this.world = world;
        this.pos = pos;
        this.amplifier = amplifier;
        this.options = options;
    }

    public World getWorld() {
        return world;
    }

    public Vector3d getPos() {
        return pos;
    }

    @Override
    public boolean run(int times) {
        ItemStackBuilder builder = game.getRegistry().getItemBuilder().fromItemStack(Probability.pickOneOf(options));
        for (int i = 0; i < amplifier.get() + 1; i++) {
            Optional<Entity> optEntity = world.createEntity(EntityTypes.DROPPED_ITEM, pos);
            if (optEntity.isPresent()) {
                Item item = (Item) optEntity.get();
                item.getItemData().setValue(builder.build());
                world.spawnEntity(item);
            }
        }
        return true;
    }

    @Override
    public void end() {

    }
}
