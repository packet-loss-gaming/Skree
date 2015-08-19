/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;

public class AttributeUtil {

    public static double getGenericAttackDamage(org.spongepowered.api.entity.Entity entity) {
        EntityLiving entity1 = (EntityLiving) entity;
        return entity1.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue();
    }

    public static boolean setGenericAttackDamage(org.spongepowered.api.entity.Entity entity, double value) {
        EntityLiving entity1 = (EntityLiving) entity;
        entity1.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(value);
        return true;
    }
}
