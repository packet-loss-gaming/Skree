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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;

public class GraveStoneTileEntity extends TileEntityLockable implements IInventory {

    private String customName;
    private ItemStack[] graveContents = new ItemStack[getSizeInventory()];

    static {
        addMapping(GraveStoneTileEntity.class, "GraveStone");
    }

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
        return 40;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.graveContents[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.graveContents[index] != null) {
            ItemStack itemstack;

            if (this.graveContents[index].stackSize <= count) {
                itemstack = this.graveContents[index];
                this.graveContents[index] = null;
                this.markDirty();
                return itemstack;
            }

            itemstack = this.graveContents[index].splitStack(count);

            if (this.graveContents[index].stackSize == 0) {
                this.graveContents[index] = null;
            }

            this.markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.graveContents[index] != null) {
            ItemStack itemstack = this.graveContents[index];
            this.graveContents[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.graveContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer playerIn) {
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
    public void clear() {
        for (int i = 0; i < getSizeInventory(); ++i) {
            this.graveContents[i] = null;
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.gravestone";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.graveContents = new ItemStack[this.getSizeInventory()];

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.graveContents.length) {
                this.graveContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.graveContents.length; ++i) {
            if (this.graveContents[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.graveContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }
}
