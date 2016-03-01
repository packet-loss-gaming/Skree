/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.hoe;

import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomHoe extends ICustomItem, DegradableItem {

    @Override
    default String __getID() {
        return __getType() + "_hoe";
    }

    @Override
    default int __getMaxStackSize() {
        return 1;
    }

    String __getType();

    int __getMaxUses();

    @Override
    default CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }

    // Modified Native ItemTool methods

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    default boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
            return false;
        } else {
            int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, playerIn, worldIn, pos);
            if (hook != 0) return hook > 0;

            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (side != EnumFacing.DOWN && worldIn.isAirBlock(pos.up())) {
                if (block == Blocks.grass) {
                    return this.__modifyBlock(stack, playerIn, worldIn, pos, Blocks.farmland.getDefaultState());
                }

                if (block == Blocks.dirt) {
                    switch (SwitchDirtType.field_179590_a[iblockstate.getValue(BlockDirt.VARIANT).ordinal()]) {
                        case 1:
                            return this.__modifyBlock(stack, playerIn, worldIn, pos, Blocks.farmland.getDefaultState());
                        case 2:
                            return this.__modifyBlock(
                                    stack,
                                    playerIn,
                                    worldIn,
                                    pos,
                                    Blocks.dirt.getDefaultState().withProperty(
                                            BlockDirt.VARIANT,
                                            BlockDirt.DirtType.DIRT
                                    )
                            );
                    }
                }
            }

            return false;
        }
    }

    default boolean __modifyBlock(ItemStack p_179232_1_, EntityPlayer p_179232_2_, World worldIn, BlockPos p_179232_4_, IBlockState p_179232_5_) {
        worldIn.playSoundEffect(
                (double) ((float) p_179232_4_.getX() + 0.5F),
                (double) ((float) p_179232_4_.getY() + 0.5F),
                (double) ((float) p_179232_4_.getZ() + 0.5F),
                p_179232_5_.getBlock().stepSound.getStepSound(),
                (p_179232_5_.getBlock().stepSound.getVolume() + 1.0F) / 2.0F,
                p_179232_5_.getBlock().stepSound.getFrequency() * 0.8F
        );

        if (worldIn.isRemote) {
            return true;
        } else {
            worldIn.setBlockState(p_179232_4_, p_179232_5_);
            p_179232_1_.damageItem(1, p_179232_2_);
            return true;
        }
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    default boolean isFull3D() {
        return true;
    }

    final class SwitchDirtType {
        static final int[] field_179590_a = new int[BlockDirt.DirtType.values().length];

        static {
            try {
                field_179590_a[BlockDirt.DirtType.DIRT.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) { }

            try {
                field_179590_a[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) { }
        }
    }
}
