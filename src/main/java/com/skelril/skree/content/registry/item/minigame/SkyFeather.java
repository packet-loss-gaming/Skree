/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.minigame;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class SkyFeather extends CustomItem {
    @Override
    public String __getID() {
        return "sky_feather";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return null;
    }

    @Override
    public int getMaxDamage(net.minecraft.item.ItemStack stack) {
        Optional<Data> optData = getDataFor(stack);
        if (!optData.isPresent()) {
            return 0;
        }

        return optData.get().uses;
    }

    @Override
    public String getHighlightTip(net.minecraft.item.ItemStack item, String displayName) {
        Optional<String> optSuffix = getSuffix(item);

        return optSuffix.isPresent() ? displayName + " [" + optSuffix.get() + "]" : displayName;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(net.minecraft.item.ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<Data> optData = getDataFor(stack);
        if (optData.isPresent()) {
            Data data = optData.get();
            tooltip.add(EnumChatFormatting.GOLD + "Uses: " + (data.uses != -1 ? data.uses : "Infinite"));
            tooltip.add(EnumChatFormatting.GOLD + "Radius: " + data.radius);
            tooltip.add(EnumChatFormatting.GOLD + "Flight: " + data.flight);
            tooltip.add(EnumChatFormatting.GOLD + "Push Back: " + data.pushBack);
        }
    }

    public static Optional<String> getSuffix(ItemStack stack) {
        return getSuffix(tf(stack));
    }

    private static Optional<String> getSuffix(net.minecraft.item.ItemStack stack) {
        Optional<Data> optData = getDataFor(stack);
        if (!optData.isPresent()) {
            return Optional.empty();
        }

        Data data = optData.get();
        int uses = data.uses;
        double flight = data.flight;
        double pushBack = data.pushBack;

        String suffix;
        if (uses == -1) {
            if (flight == pushBack && flight > 2) {
                suffix = "Doom";
            } else {
                suffix = "Infinite";
            }
        } else {
            if (flight == pushBack) {
                suffix = "Balance";
            } else if (flight > pushBack) {
                suffix = "Flight";
            } else {
                suffix = "Push Back";
            }
        }

        return Optional.of(suffix);
    }

    public static Optional<Data> getDataFor(ItemStack stack) {
        return getDataFor(tf(stack));
    }

    private static Optional<Data> getDataFor(net.minecraft.item.ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return Optional.empty();
        }

        if (!stack.getTagCompound().hasKey("skree_feather_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_feather_data");
        int uses = tag.getInteger("uses");
        double radius = tag.getDouble("radius");
        double flight = tag.getDouble("flight");
        double pushBack = tag.getDouble("push_back");

        return Optional.of(new Data(uses, radius, flight, pushBack));
    }

    public static void setFeatherProperties(ItemStack stack, int uses, double radius, double flight, double pushBack) {
        setFeatherProperties(tf(stack), uses, radius, flight, pushBack);
    }

    private static void setFeatherProperties(net.minecraft.item.ItemStack stack, int uses, double radius, double flight, double pushBack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_feather_data")) {
            stack.getTagCompound().setTag("skree_feather_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_feather_data");
        tag.setInteger("uses", uses);
        tag.setDouble("radius", radius);
        tag.setDouble("flight", flight);
        tag.setDouble("push_back", pushBack);
    }

    public static class Data {
        public final int uses;
        public final double radius;
        public final double flight;
        public final double pushBack;

        public Data(int uses, double radius, double flight, double pushBack) {
            this.uses = uses;
            this.radius = radius;
            this.flight = flight;
            this.pushBack = pushBack;
        }
    }
}
