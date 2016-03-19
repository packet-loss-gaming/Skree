/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.trap;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Pitfall extends Block implements ICustomBlock, Craftable {

    public Pitfall() {
        super(Material.clay);
        this.setBlockBounds(0F, .9375F, 0F, 1F, 1F, 1F);
        this.setCreativeTab(CreativeTabs.tabDecorations);

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(0.6F);
        this.setStepSound(soundTypePiston);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public String __getID() {
        return "pitfall";
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this, 3),
                new ItemStack(Blocks.clay),
                new ItemStack(CustomItemTypes.FAIRY_DUST)
        );
    }
}

