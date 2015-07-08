/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public abstract class CustomFood extends ItemFood implements ICustomFood {

    protected boolean alwaysEditable = false;
    protected boolean lovedByWolves = false;

    protected CustomFood() {
        super(0, false);
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());
    }

    @Override
    public ItemFood __getFoodItem() {
        return this;
    }

    @Override
    public boolean __isAlwaysEditable() {
        return alwaysEditable;
    }

    @Override
    public ItemFood __setAlwaysEditable(boolean alwaysEditable) {
        this.alwaysEditable = alwaysEditable;
        return this;
    }

    @Override
    public boolean __isLovedBy(EntityType type) {
        if (type == EntityTypes.WOLF) {
            return lovedByWolves;
        }
        return false;
    }

    @Override
    public void __onFoodEaten(ItemStack p_77849_1_, World worldIn, EntityPlayer p_77849_3_) {
        super.onFoodEaten(p_77849_1_, worldIn, p_77849_3_);
    }

    @Override
    public ItemFood __setPotionEffect(int p_77844_1_, int duration, int amplifier, float probability) {
        return super.setPotionEffect(p_77844_1_, duration, amplifier, probability);
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return ICustomFood.super.onItemUseFinish(stack, worldIn, playerIn);
    }

    @Override
    protected void onFoodEaten(ItemStack p_77849_1_, World worldIn, EntityPlayer p_77849_3_) {
        __onFoodEaten(p_77849_1_, worldIn, p_77849_3_);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return ICustomFood.super.getMaxItemUseDuration(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return ICustomFood.super.getItemUseAction(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return ICustomFood.super.onItemRightClick(itemStackIn, worldIn, playerIn);
    }

    @Override
    public int getHealAmount(ItemStack itemStackIn) {
        return ICustomFood.super.getHealAmount(itemStackIn);
    }

    @Override
    public float getSaturationModifier(ItemStack itemStackId) {
        return ICustomFood.super.getSaturationModifier(itemStackId);
    }

    @Override
    public boolean isWolfsFavoriteMeat() {
        return ICustomFood.super.isWolfsFavoriteMeat();
    }

    @Override
    public ItemFood setPotionEffect(int p_77844_1_, int duration, int amplifier, float probability) {
        return super.setPotionEffect(p_77844_1_, duration, amplifier, probability);
    }

    @Override
    public ItemFood setAlwaysEdible() {
        return ICustomFood.super.setAlwaysEdible();
    }
}
