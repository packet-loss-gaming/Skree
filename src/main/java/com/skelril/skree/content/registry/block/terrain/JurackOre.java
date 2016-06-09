/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.terrain;

import com.skelril.nitro.ReflectiveModifier;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.nitro.registry.block.OreHelper;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class JurackOre extends BlockOre implements ICustomBlock {

    public JurackOre() {
        // Refers to blockMaterial
        ReflectiveModifier.modifyFieldValue(Block.class, this, "field_149764_J", OreHelper.CUSTOM_ORE_MATERIAL);

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public String __getID() {
        return "jurack_ore";
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return CustomItemTypes.JURACK_GEM;
    }
}
