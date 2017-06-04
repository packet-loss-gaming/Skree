/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.bow;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CustomBow extends ItemBow implements ICustomBow {
  public CustomBow() {
    this.maxStackSize = __getMaxStackSize();
    this.setCreativeTab(__getCreativeTab());

    this.setMaxDamage(__getMaxUses());

    this.addPropertyOverride(new ResourceLocation("skree", "pull"), new IItemPropertyGetter() {
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack item, @Nullable World world, @Nullable EntityLivingBase living) {
        if (living == null) {
          return 0.0F;
        } else {
          ItemStack itemstack = living.getActiveItemStack();
          return itemstack != null ? (item.getMaxItemUseDuration() - living.getItemInUseCount()) / 20.0F : 0.0F;
        }
      }
    });
    this.addPropertyOverride(new ResourceLocation("skree", "pulling"), new IItemPropertyGetter() {
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack item, @Nullable World world, @Nullable EntityLivingBase living) {
        return living != null && living.isHandActive() && living.getActiveItemStack() == item ? 1.0F : 0.0F;
      }
    });
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(net.minecraft.item.Item itemIn, CreativeTabs tab, List subItems) {
    ICustomBow.super.getSubItems(itemIn, tab, subItems);
  }

  @Override
  public boolean isArrow(@Nullable ItemStack stack) {
    return ICustomBow.super.isArrow(stack);
  }

  @Override
  public ItemStack findAmmo(EntityPlayer player) {
    return ICustomBow.super.findAmmo(player);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    ICustomBow.super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
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
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    return ICustomBow.super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
  }

  @Override
  public int getItemEnchantability() {
    return ICustomBow.super.getItemEnchantability();
  }

}
