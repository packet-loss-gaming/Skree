/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.tool;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ExpansionCharm extends BlockPatternCharm {
    public ExpansionCharm() {
        super(2, "expansion", 1);
    }

    @Override
    protected void process(ArmorEquipable holder, ItemStack stack, Location<World> pos, Direction direction, int power) {

    }
}
