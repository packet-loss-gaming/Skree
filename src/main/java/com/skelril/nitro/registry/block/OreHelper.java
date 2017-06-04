/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.block;

import com.skelril.nitro.ReflectiveModifier;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class OreHelper {
  public static final Material CUSTOM_ORE_MATERIAL = new Material(MapColor.GRAY);

  static {
    // Refers to requiresNoTool
    ReflectiveModifier.modifyFieldValue(Material.class, CUSTOM_ORE_MATERIAL, "field_76241_J", false);
  }
}
