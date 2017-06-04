/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.tweaker;

import com.skelril.nitro.ReflectiveModifier;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;

@NModule(name = "Tweaker System")
public class TweakerSystem {
  @NModuleTrigger(trigger = "PRE_INITIALIZATION")
  public void preInit() {
    ReflectiveModifier.modifyFieldValue(RangedAttribute.class, (RangedAttribute) SharedMonsterAttributes.MAX_HEALTH, "field_111118_b", Double.MAX_VALUE);
  }
}