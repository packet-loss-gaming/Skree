/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FairyDust extends CustomItem implements Craftable {

    @Override
    public String __getID() {
        return "fairy_dust";
    }

    @Override
    public int __getMaxStackSize() {
        return 16;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.MATERIALS;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this),
                new ItemStack(CustomBlockTypes.MAGIC_MUSHROOM)
        );
    }
}
