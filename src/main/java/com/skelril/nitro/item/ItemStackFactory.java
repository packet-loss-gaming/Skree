/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.skelril.nitro.registry.item.ICustomItem;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemStackFactory {

  public static ItemStack newItemStack(BlockType type) {
    return newItemStack(type.getItem().get());
  }

  public static ItemStack newItemStack(BlockType type, DataManipulator<?, ?> data) {
    return newItemStack(type.getItem().get(), data);
  }

  public static ItemStack newItemStack(BlockType type, int quantity) {
    return newItemStack(type.getItem().get(), quantity);
  }

  public static ItemStack newItemStack(BlockType type, DataManipulator<?, ?> data, int quantity) {
    return newItemStack(type.getItem().get(), data, quantity);
  }

  public static ItemStack newItemStack(ICustomItem type) {
    return newItemStack((ItemType) type);
  }

  public static ItemStack newItemStack(ICustomItem type, DataManipulator<?, ?> data) {
    return newItemStack((ItemType) type, data);
  }

  public static ItemStack newItemStack(ICustomItem type, int quantity) {
    return newItemStack((ItemType) type, quantity);
  }

  public static ItemStack newItemStack(ICustomItem type, DataManipulator<?, ?> data, int quantity) {
    return newItemStack((ItemType) type, data, quantity);
  }

  public static ItemStack newItemStack(ItemType type) {
    return newItemStack(type, 1);
  }

  public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data) {
    return newItemStack(type, data, 1);
  }

  public static ItemStack newItemStack(ItemType type, int quantity) {
    return ItemStack.builder().itemType(type).quantity(quantity).build();
  }

  public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data, int quantity) {
    return ItemStack.builder().itemType(type).quantity(quantity).itemData(data).build();
  }

  public static ItemStack newItemStack(String typeName) {
    return newItemStack(typeName, 1);
  }

  public static ItemStack newItemStack(String typeName, DataManipulator<?, ?> data) {
    return newItemStack(typeName, data, 1);
  }

  public static ItemStack newItemStack(String typeName, int quantity) {
    return ItemStack.builder().itemType(typeFromString(typeName)).quantity(quantity).build();
  }

  public static ItemStack newItemStack(String typeName, DataManipulator<?, ?> data, int quantity) {
    return ItemStack.builder().itemType(typeFromString(typeName)).quantity(quantity).itemData(data).build();
  }

  public static ItemStack newItemStack(ItemStack itemStack) {
    return itemStack.copy();
  }

  public static ItemStack newItemStack(ItemStack itemStack, int quantity) {
    ItemStack stack = itemStack.copy();
    stack.setQuantity(quantity);
    return stack;
  }

  public static Collection<ItemStack> newItemStackCollection(ItemStack itemStack, int quantity) {
    List<ItemStack> itemStacks = new ArrayList<>();
    for (int i = quantity; i > 0; ) {
      int diff = Math.min(i, itemStack.getMaxStackQuantity());
      i -= diff;
      itemStacks.add(newItemStack(itemStack, quantity));
    }
    return itemStacks;
  }

  private static ItemType typeFromString(String typeName) {
    return Sponge.getRegistry().getType(ItemType.class, typeName).get();
  }
}
