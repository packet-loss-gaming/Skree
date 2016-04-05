/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.utility;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicPlatform extends Block implements ICustomBlock, Craftable {
    public MagicPlatform() {
        super(Material.wood);
        this.setBlockBounds(0F, .9375F, 0F, 1F, 1F, 1F);
        this.setCreativeTab(CreativeTabs.tabDecorations);

        // Data applied for Vanilla blocks in net.minecraft.block.Block
        this.setHardness(0.4F);
        this.setStepSound(soundTypeWood);
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
        return "magic_platform";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AAA",
                " B ",
                "   ",
                'A', new ItemStack(Items.stick),
                'B', new ItemStack(CustomItemTypes.FAIRY_DUST)
        );
    }
}
