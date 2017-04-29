/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.bow;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

class LoadedBow extends ItemBow {
    private BowConfig config;

    public LoadedBow(BowConfig config) {
        this.config = config;

        setMaxDamage();
        setMaxStackSize();
        setCreativeTab();

        applyPropertyOverrides();
    }

    // Config Loading

    private void setMaxDamage() {
        this.setMaxDamage(config.getMaxUses());
    }

    private void setMaxStackSize() {
        this.setMaxStackSize(1);
    }

    private void setCreativeTab() {
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    private void applyPropertyOverrides() {
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
}
