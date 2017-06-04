/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.utility;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.block.ICustomBlock;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class MagicLadder extends BlockLadder implements ICustomBlock, Craftable {

  public MagicLadder() {
    super();

    // Data applied for Vanilla blocks in net.minecraft.block.Block
    this.setHardness(0.4F);
    this.setSoundType(SoundType.LADDER);
  }

  @Override
  public String __getID() {
    return "magic_ladder";
  }

  @Override
  public void registerRecipes() {
    GameRegistry.addShapelessRecipe(
        new ItemStack(this),
        new ItemStack(Blocks.LADDER),
        newItemStack("skree:fairy_dust")
    );
  }

  @SideOnly(Side.CLIENT)
  @Override
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }
}
