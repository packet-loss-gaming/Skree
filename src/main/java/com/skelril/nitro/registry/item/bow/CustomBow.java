/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.bow;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class CustomBow extends ItemBow implements ICustomBow {
    public CustomBow() {
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        this.setMaxDamage(__getMaxUses());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.resources.model.ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
        return ICustomBow.super.getModel(stack, player, useRemaining);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(net.minecraft.item.Item itemIn, CreativeTabs tab, List subItems) {
        ICustomBow.super.getSubItems(itemIn, tab, subItems);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        ICustomBow.super.onPlayerStoppedUsing(stack, worldIn, playerIn, timeLeft);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return ICustomBow.super.onItemUseFinish(stack, worldIn, playerIn);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return ICustomBow.super.getMaxItemUseDuration(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return ICustomBow.super.getItemUseAction(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return ICustomBow.super.onItemRightClick(itemStackIn, worldIn, playerIn);
    }

    @Override
    public int getItemEnchantability() {
        return ICustomBow.super.getItemEnchantability();
    }

}
