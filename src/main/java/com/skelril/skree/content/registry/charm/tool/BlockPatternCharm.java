/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.tool;

import com.skelril.skree.content.registry.charm.AbstractCharm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;

public abstract class BlockPatternCharm extends AbstractCharm {

    public BlockPatternCharm(int ID, String name, int maxLevel) {
        super(ID, name, maxLevel);
    }

    protected boolean accepts(ItemStack stack, BlockState state) {
        return ((Item) stack.getItem()).getDigSpeed(
                (net.minecraft.item.ItemStack) (Object) stack,
                (IBlockState) state
        ) > 1;
    }

    protected boolean hasSameTraits(Map<?, ?> a, Map<?, ?> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (Map.Entry<?, ?> entry : a.entrySet()) {
            if (!b.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    protected boolean breakBlock(ArmorEquipable holder, Location<World> target) {
        target.setBlockType(BlockTypes.AIR);
        return true;
    }

    protected abstract void process(ArmorEquipable holder, ItemStack stack, Location<World> pos, Direction direction, int power);
}
