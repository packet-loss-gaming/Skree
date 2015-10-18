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

public class ItemFountain implements IntegratedRunnable  {

    private final ItemDropper dropper;

    private final Generator<Integer> amplifier;
    private final Collection<ItemStack> options;

    public ItemFountain(Game game, World world, Vector3d pos, Generator<Integer> amplifier, Collection<ItemStack> options) {
        this.dropper = new ItemDropper(game, world, pos);

        this.amplifier = amplifier;
        this.options = options;
    }

    public World getExtent() {
        return dropper.getExtent();
    }

    public Vector3d getPos() {
        return dropper.getPos();
    }

    @Override
    public boolean run(int times) {
        ItemStack stack = Probability.pickOneOf(options);
        for (int i = 0; i < amplifier.get() + 1; i++) {
            dropper.dropItems(Collections.singletonList(stack));
        }
        return true;
    }

    @Override
    public void end() {

    }
}
