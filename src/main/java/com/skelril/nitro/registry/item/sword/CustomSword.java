/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.CustomTool;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public abstract class CustomSword extends ItemSword implements CustomTool {

    public CustomSword(ToolMaterial p_i45356_1_) {
        super(p_i45356_1_);
    }

    public abstract String getType();

    public abstract double getDamage();

    @Override
    public String getID() {
        String typeStr = getType();
        return "sword" + Character.toUpperCase(typeStr.charAt(0)) + typeStr.substring(1);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(itemModifierUUID, "Weapon modifier", getDamage(), 0)
        );
        return multimap;
    }

}
