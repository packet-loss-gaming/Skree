/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.sword;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.sword.CustomSword;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class CrystalSword extends CustomSword {
    public CrystalSword() {
        super(ToolMaterial.EMERALD);
        setUnlocalizedName(getID());
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

    @Override
    public String getType() {
        return "crystal";
    }

    @Override
    public double getDamage() {
        return 8;
    }
}
