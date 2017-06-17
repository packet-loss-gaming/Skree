/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.block.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;

public class GraveStoneTileEntity extends TileEntityLockableLoot {

  private String customName;
  private NonNullList<ItemStack> graveContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

  @Override
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
    return new ContainerChest(playerInventory, this, playerIn);
  }

  @Override
  public String getGuiID() {
    return "minecraft:chest";
  }

  @Override
  public int getSizeInventory() {
    return 80;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemstack : this.graveContents) {
      if (!itemstack.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {
    return false;
  }

  @Override
  public void openInventory(EntityPlayer playerIn) {

  }

  @Override
  public void closeInventory(EntityPlayer playerIn) {

  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return true;
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {

  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.graveContents;
  }

  @Override
  public String getName() {
    return this.hasCustomName() ? this.customName : "container.gravestone";
  }

  @Override
  public boolean hasCustomName() {
    return this.customName != null && this.customName.length() > 0;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.graveContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

    ItemStackHelper.loadAllItems(compound, this.graveContents);

    if (compound.hasKey("CustomName", 8)) {
      this.customName = compound.getString("CustomName");
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);

    ItemStackHelper.saveAllItems(compound, this.graveContents);

    if (this.hasCustomName()) {
      compound.setString("CustomName", this.customName);
    }

    return compound;
  }}
