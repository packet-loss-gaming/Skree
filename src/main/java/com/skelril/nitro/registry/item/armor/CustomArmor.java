/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.armor;

import com.skelril.nitro.ReflectiveModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CustomArmor extends ItemArmor implements ICustomArmor {

    public CustomArmor(int armorType) {
        super(ArmorMaterial.DIAMOND, 4, armorType); // 4 is allegedly used for net.minecraft.client.renderer.entity.RenderPlayer
                                                    // to determine which model to use
                                                    // This just bases everything off the diamond armor model

        // Refers to damageReduceAmount
        ReflectiveModifier.modifyFieldValue(ItemArmor.class, this, "field_77879_b", __getDamageReductionAmount());

        this.setMaxDamage(__getMaxUses());
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());
    }

    // Native compatibility methods

    @Override
    public ItemArmor.ArmorMaterial __superGetArmorMaterial() {
        return super.getArmorMaterial();
    }

    @Override
    public boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false; // Use functionality defined in Item
    }

    @Override
    public int __getArmorTypeIndex() {
        return armorType;
    }

    // Modified Native ItemArmor methods

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return ICustomArmor.super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return ICustomArmor.super.getColorFromItemStack(stack, renderPass);
    }

    @Override
    public int getItemEnchantability() {
        return ICustomArmor.super.getItemEnchantability();
    }

    @Override
    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return ICustomArmor.super.getArmorMaterial();
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return ICustomArmor.super.hasColor(stack);
    }

    @Override
    public int getColor(ItemStack stack) {
        return ICustomArmor.super.getColor(stack);
    }

    @Override
    public void removeColor(ItemStack stack) {
        ICustomArmor.super.removeColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        ICustomArmor.super.setColor(stack, color);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ICustomArmor.super.getIsRepairable(toRepair, repair);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return ICustomArmor.super.onItemRightClick(itemStackIn, worldIn, playerIn);
    }
}
