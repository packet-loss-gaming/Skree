/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.generator.Generator;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import org.spongepowered.api.Game;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class ItemFountain extends ItemDropper implements IntegratedRunnable  {

    private static Random random = new Random();

    private Game game;
    private World world;
    private Vector3d pos;
    private Generator<Integer> amplifier;
    private Collection<ItemStack> options;

    public ItemFountain(Game game, World world, Vector3d pos, Generator<Integer> amplifier, Collection<ItemStack> options) {
        super(game, world, pos);
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
        ItemStack stack = Probability.pickOneOf(options);
        for (int i = 0; i < amplifier.get() + 1; i++) {
            dropItems(Collections.singletonList(stack));
        }
        return true;
    }

    @Override
    public void end() {

    }
}
