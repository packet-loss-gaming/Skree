/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CharmTools {

    private static ItemStack cast(org.spongepowered.api.item.inventory.ItemStack stack) {
        return (ItemStack) ((Object) stack);
    }

    public static int getLevel(org.spongepowered.api.item.inventory.ItemStack stack, Charm charm) {
        return getLevel(cast(stack), charm);
    }

    public static int getLevel(ItemStack stack, Charm charm) {
        if (hasCharms(stack)) {
            NBTTagList list = stack.getTagCompound().getTagList("skree_charm", 10);
            if (list != null) {
                for (int i = 0; i < list.tagCount(); ++i) {
                    short entry = list.getCompoundTagAt(i).getShort("id");
                    if (entry == charm.getID()) {
                        return list.getCompoundTagAt(i).getShort("lvl");
                    }
                }
            }
        }
        return 0;
    }

    public static int addCharm(org.spongepowered.api.item.inventory.ItemStack stack, Charm charm, int level) {
        addCharm(cast(stack), charm, level);
    }

    public static void addCharm(ItemStack itemStack, Charm charm, int level) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        if (!itemStack.getTagCompound().hasKey("skree_charm", 9)) {
            itemStack.getTagCompound().setTag("skree_charm", new NBTTagList());
        }

        NBTTagList nbttaglist = itemStack.getTagCompound().getTagList("skree_charm", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short) charm.getID());
        nbttagcompound.setShort("lvl", (short) level);
        nbttaglist.appendTag(nbttagcompound);
    }

    public static boolean hasCharm(org.spongepowered.api.item.inventory.ItemStack stack) {
        return hasCharms(cast(stack));
    }

    public static boolean hasCharms(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("skree_charm", 9);
    }
}
