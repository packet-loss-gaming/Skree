/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public abstract class CustomSword extends ItemSword implements ICustomSword {
    protected CustomSword() {
        super(ToolMaterial.EMERALD);
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        this.setMaxDamage(__getMaxUses());
    }

    // Native compatibility methods

    @Override
    public boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false; // Use functionality defined in Item
    }

    @Override
    public Multimap __superGetItemAttributeModifiers() {
        return HashMultimap.create(); // Use functionality defined in Item
    }

    @Override
    public UUID __itemModifierUUID() {
        return itemModifierUUID;
    }

    // Modified Native ItemTool methods
    @Override
    public float getStrVsBlock(ItemStack stack, Block p_150893_2_) {
        return ICustomSword.super.getStrVsBlock(stack, p_150893_2_);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return ICustomSword.super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        return ICustomSword.super.onBlockDestroyed(stack, worldIn, blockIn, pos, playerIn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return ICustomSword.super.isFull3D();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return ICustomSword.super.getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return ICustomSword.super.getMaxItemUseDuration(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return ICustomSword.super.onItemRightClick(itemStackIn, worldIn, playerIn);
    }

    @Override
    public boolean canHarvestBlock(Block blockIn) {
        return ICustomSword.super.canHarvestBlock(blockIn);
    }

    @Override
    public int getItemEnchantability() {
        return ICustomSword.super.getItemEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ICustomSword.super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return ICustomSword.super.getItemAttributeModifiers();
    }

}
