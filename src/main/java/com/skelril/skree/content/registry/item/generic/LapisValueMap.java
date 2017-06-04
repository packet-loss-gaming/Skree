/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.skelril.nitro.point.ItemStackBigIntegerValueMapping;
import com.skelril.nitro.point.PointValue;
import com.skelril.nitro.point.SimplePointValue;
import net.minecraft.init.Items;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigInteger;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class LapisValueMap extends ItemStackBigIntegerValueMapping {
  public static final ImmutableList<PointValue<ItemStack, BigInteger>> LAPIS_VALUE_MAP = ImmutableList.of(
      new SimplePointValue<>(
          Lists.newArrayList(tf(new net.minecraft.item.ItemStack(Items.DYE, 1, 4))),
          BigInteger.ONE
      ),
      new SimplePointValue<>(
          Lists.newArrayList(newItemStack(ItemTypes.LAPIS_BLOCK)),
          BigInteger.valueOf(9)
      )
  );


  private static final LapisValueMap MAP = new LapisValueMap();

  protected LapisValueMap() {
    super(LAPIS_VALUE_MAP);
  }

  public static LapisValueMap inst() {
    return MAP;
  }
}