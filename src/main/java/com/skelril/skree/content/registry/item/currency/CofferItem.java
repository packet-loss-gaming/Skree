/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.currency;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;

import java.text.DecimalFormat;
import java.util.List;

public class CofferItem extends CustomItem {
  private final String id;
  private final int cofferValue;

  public CofferItem(String id, int cofferValue) {
    super();
    Validate.isTrue(cofferValue >= 1, "Currency can now be worth less than 1 coffer");
    this.id = id.toLowerCase();
    this.cofferValue = cofferValue;
  }

  public int getCofferValue() {
    return cofferValue;
  }

  @Override
  public String __getId() {
    return id + "_coffer";
  }

  @Override
  public int __getMaxStackSize() {
    return 64;
  }

  @Override
  public CreativeTabs __getCreativeTab() {
    return CreativeTabs.MISC;
  }

  // Modified Native Item methods

  @SuppressWarnings("unchecked")
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    DecimalFormat formatter = new DecimalFormat("#,###");
    tooltip.add(formatter.format(getCofferValue()) + " Coffers Each");
    if (stack.stackSize > 1) {
      tooltip.add(formatter.format(stack.stackSize * getCofferValue()) + " Coffers Total");
    }
  }
}
