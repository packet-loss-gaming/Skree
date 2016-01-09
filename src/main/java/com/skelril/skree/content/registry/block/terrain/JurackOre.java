/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.terrain;

import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.Random;

public class JurackOre extends BlockOre implements ICustomBlock {

    public JurackOre() {
        this.setCreativeTab(CreativeTabs.tabBlock);

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypePiston);
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
