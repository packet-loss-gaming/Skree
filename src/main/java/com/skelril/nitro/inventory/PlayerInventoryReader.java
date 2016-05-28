/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.inventory;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

/**
 * WARNING The methods in this file are incredibly dangerous!!!
 */
public class PlayerInventoryReader {

    private static File getPlayerFile(UUID playerId) {
        File worldDirectory = DimensionManager.getCurrentSaveRootDirectory();
        File playerDataDirectory = new File(worldDirectory, "playerdata");

        return new File(playerDataDirectory, playerId + ".dat");
    }

    private static Optional<NBTTagCompound> readPlayerData(UUID playerId) {
        File file = getPlayerFile(playerId);

        if (file.exists() && file.isFile()) {
            try {
                return Optional.of(CompressedStreamTools.readCompressed(new FileInputStream(file)));
            } catch (IOException e) {
            }
        }
        return Optional.empty();
    }

    private static boolean writePlayerData(NBTTagCompound nbtTagCompound, UUID playerId) {
        File file = getPlayerFile(playerId);

        if (file.exists() && file.isFile()) {
            try {
                CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(file));
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

    private static void readInventory(NBTTagList inventoryTag, Data output) {
        output.mainInventory = new ItemStack[36];
        output.armorInventory = new ItemStack[4];

        for (int i = 0; i < inventoryTag.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = inventoryTag.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            net.minecraft.item.ItemStack itemstack = net.minecraft.item.ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null) {
                if (j >= 0 && j < output.mainInventory.length) {
                    output.mainInventory[j] = tf(itemstack);
                }

                if (j >= 100 && j < output.armorInventory.length + 100) {
                    output.armorInventory[j - 100] = tf(itemstack);
                }
            }
        }
    }

    private static void writeInventory(NBTTagCompound nbtTagCompound, Data input) {
        NBTTagList inventoryTag = new NBTTagList();

        for (int i = 0; i < input.mainInventory.length; ++i) {
            if (input.mainInventory[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                tf(input.mainInventory[i]).writeToNBT(nbttagcompound);
                inventoryTag.appendTag(nbttagcompound);
            }
        }

        for (int j = 0; j < input.armorInventory.length; ++j) {
            if (input.armorInventory[j] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)(j + 100));
                tf(input.armorInventory[j]).writeToNBT(nbttagcompound1);
                inventoryTag.appendTag(nbttagcompound1);
            }
        }

        nbtTagCompound.setTag("Inventory", inventoryTag);
    }

    private static void readEnderChest(NBTTagList inventoryTag, Data output) {
        output.enderInventory = new ItemStack[27];

        for (int i = 0; i < output.enderInventory.length; ++i) {
            output.enderInventory[i] = null;
        }

        for (int k = 0; k < inventoryTag.tagCount(); ++k) {
            NBTTagCompound nbttagcompound = inventoryTag.getCompoundTagAt(k);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < output.enderInventory.length) {
                output.enderInventory[j] = tf(net.minecraft.item.ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }

    private static void writeEnderChest(NBTTagCompound nbtTagCompound, Data input) {
        NBTTagList inventoryTag = new NBTTagList();

        for (int i = 0; i < input.enderInventory.length; ++i) {
            net.minecraft.item.ItemStack itemstack = tf(input.enderInventory[i]);

            if (itemstack != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                inventoryTag.appendTag(nbttagcompound);
            }
        }

        nbtTagCompound.setTag("EnderItems", inventoryTag);
    }

    public static Data getPlayerData(UUID id) {
        Optional<NBTTagCompound> optTag = readPlayerData(id);
        if (!optTag.isPresent()) {
            return null;
        }

        Data data = new Data();
        NBTTagCompound tagCompound = optTag.get();

        readInventory(tagCompound.getTagList("Inventory", 10), data);

        if (tagCompound.hasKey("EnderItems", 9)) {
            readEnderChest(tagCompound.getTagList("EnderItems", 10), data);
        }

        return data;
    }

    public static void writePlayerData(UUID id, Data data) {
        Optional<NBTTagCompound> optTag = readPlayerData(id);
        if (!optTag.isPresent()) {
            return;
        }

        NBTTagCompound tagCompound = optTag.get();

        writeInventory(tagCompound, data);

        if (data.enderInventory != null) {
            writeEnderChest(tagCompound, data);
        }

        writePlayerData(tagCompound, id);
    }

    public static class Data {
        public ItemStack[] mainInventory;
        public ItemStack[] armorInventory;
        public ItemStack[] enderInventory;
    }
}
