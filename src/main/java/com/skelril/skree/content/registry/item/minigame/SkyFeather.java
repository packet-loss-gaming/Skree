/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.minigame;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class SkyFeather extends CustomItem {
  @Override
  public String __getId() {
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
    return optData.map(data -> data.chanceOfLoss).orElse(0);

  }

  @Override
  public String getHighlightTip(net.minecraft.item.ItemStack item, String displayName) {
    Optional<String> optSuffix = getSuffix(item);

    return optSuffix.isPresent() ? displayName + " [" + optSuffix.get() + "]" : displayName;
  }

  @SuppressWarnings("unchecked")
  @SideOnly(Side.CLIENT)
  public void addInformation(net.minecraft.item.ItemStack stack, @Nullable net.minecraft.world.World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    Optional<Data> optData = getDataFor(stack);
    if (optData.isPresent()) {
      Data data = optData.get();
      tooltip.add(ChatFormatting.GOLD + "Chance of Loss: " + (data.chanceOfLoss != -1 ? "1 / " + data.chanceOfLoss : "0"));
      tooltip.add(ChatFormatting.GOLD + "Radius: " + data.radius);
      tooltip.add(ChatFormatting.GOLD + "Flight: " + data.flight);
      tooltip.add(ChatFormatting.GOLD + "Push Back: " + data.pushBack);
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
    int chanceOfLoss = data.chanceOfLoss;
    double flight = data.flight;
    double pushBack = data.pushBack;

    String suffix;
    if (chanceOfLoss == -1) {
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
    int chanceOfLoss = tag.getInteger("chance_of_loss");
    double radius = tag.getDouble("radius");
    double flight = tag.getDouble("flight");
    double pushBack = tag.getDouble("push_back");

    return Optional.of(new Data(chanceOfLoss, radius, flight, pushBack));
  }

  public static void setFeatherProperties(ItemStack stack, int chanceOfLoss, double radius, double flight, double pushBack) {
    setFeatherProperties(tf(stack), chanceOfLoss, radius, flight, pushBack);
  }

  private static void setFeatherProperties(net.minecraft.item.ItemStack stack, int chanceOfLoss, double radius, double flight, double pushBack) {
    if (stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }

    if (!stack.getTagCompound().hasKey("skree_feather_data")) {
      stack.getTagCompound().setTag("skree_feather_data", new NBTTagCompound());
    }

    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_feather_data");
    tag.setInteger("chance_of_loss", chanceOfLoss);
    tag.setDouble("radius", radius);
    tag.setDouble("flight", flight);
    tag.setDouble("push_back", pushBack);
  }

  public static class Data {
    public final int chanceOfLoss;
    public final double radius;
    public final double flight;
    public final double pushBack;

    public Data(int chanceOfLoss, double radius, double flight, double pushBack) {
      this.chanceOfLoss = chanceOfLoss;
      this.radius = radius;
      this.flight = flight;
      this.pushBack = pushBack;
    }
  }
}
