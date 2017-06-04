/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.trap;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class Pitfall extends Block implements ICustomBlock, Craftable {

  public Pitfall() {
    super(Material.CLAY);
    this.setCreativeTab(CreativeTabs.DECORATIONS);

    // Data applied for Vanilla blocks in net.minecraft.block.Block
    this.setHardness(0.6F);
    this.setSoundType(SoundType.STONE);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return new AxisAlignedBB(0F, .9375F, 0F, 1F, 1F, 1F);
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
    return true;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public String __getID() {
    return "pitfall";
  }

  @Override
  public void registerRecipes() {
    GameRegistry.addShapelessRecipe(
        new ItemStack(this, 3),
        new ItemStack(Blocks.CLAY),
        newItemStack("skree:fairy_dust")
    );
  }
}

