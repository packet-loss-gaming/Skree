/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import com.skelril.skree.system.registry.recipe.CustomRecipeSystem;

@NModule(name = "Custom Registry System")
public class CustomRegisterySystem {
  private CustomItemSystem customItemSystem = new CustomItemSystem();
  private CustomBlockSystem customBlockSystem = new CustomBlockSystem();
  private CustomRecipeSystem customRecipeSystem = new CustomRecipeSystem();

  @NModuleTrigger(trigger = "PRE_INITIALIZATION")
  public void preInit() {
    customItemSystem = new CustomItemSystem();
    customItemSystem.preInit();

    customBlockSystem = new CustomBlockSystem();
    customBlockSystem.preInit();

    customItemSystem.associate();
    customBlockSystem.associate();

    customRecipeSystem = new CustomRecipeSystem();
    customRecipeSystem.associate();
  }

  @NModuleTrigger(trigger = "FMLInitializationEvent")
  public void init() {
    customItemSystem.init();
    customBlockSystem.init();
  }
}
