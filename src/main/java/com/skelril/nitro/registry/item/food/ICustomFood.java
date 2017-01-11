/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.food;

import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public interface ICustomFood extends ICustomItem {

    // Skelril Methods

    // General

    @Override
    default CreativeTabs __getCreativeTab() {
        return CreativeTabs.FOOD;
    }

    ItemFood __getFoodItem();

    boolean __isAlwaysEditable();
    ItemFood __setAlwaysEditable(boolean alwaysEditable);

    double __getHealAmount();

    double __getSaturationModifier();

    boolean __isLovedBy(EntityType type);

    // Native compatibility methods

    void __onFoodEaten(ItemStack p_77849_1_, World worldIn, EntityPlayer p_77849_3_);

    /**
     * sets a potion effect on the item. Args: int potionId, int duration (will be multiplied by 20), int amplifier,
     * float probability of effect happening
     */
    ItemFood __setPotionEffect(PotionEffect p_185070_1_, float p_185070_2_);

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    default ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        stack.setCount(stack.getCount() - 1);

        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            entityplayer.getFoodStats().addStats(__getFoodItem(), stack);
            worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            this.__onFoodEaten(stack, worldIn, entityplayer);
            entityplayer.addStat(StatList.getObjectUseStats(__getFoodItem()));
        }

        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    default int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    default EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    default ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (playerIn.canEat(__isAlwaysEditable())) {
            playerIn.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
        }
    }

    default int getHealAmount(ItemStack itemStackIn) {
        return (int) Math.round(__getHealAmount());
    }

    default float getSaturationModifier(ItemStack itemStackId) {
        return (float) __getSaturationModifier();
    }

    /**
     * Whether wolves like this food (true for raw and cooked porkchop).
     */
    default boolean isWolfsFavoriteMeat() {
        return __isLovedBy(EntityTypes.WOLF);
    }

    /**
     * Set the field 'alwaysEdible' to true, and make the food edible even if the player don't need to eat.
     */
    default ItemFood setAlwaysEdible() {
        return __setAlwaysEditable(true);
    }
}
