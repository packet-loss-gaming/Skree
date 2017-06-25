/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.skree.content.registry.ability.SkreeAbilityRegistry;
import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import com.skelril.skree.system.registry.recipe.CustomRecipeSystem;
import com.skelril.skree.system.registry.seteffect.CustomSetEffectSystem;

@NModule(name = "Custom Registry System")
public class CustomRegisterySystem {
  private GameIntegrator gameIntegrator = new GameIntegrator("skree");
  private SkreeAbilityRegistry abilityRegistry = new SkreeAbilityRegistry();

  private CustomItemSystem customItemSystem = new CustomItemSystem(gameIntegrator, abilityRegistry);
  private CustomSetEffectSystem customSetEffectSystem = new CustomSetEffectSystem(gameIntegrator, abilityRegistry);
  private CustomBlockSystem customBlockSystem = new CustomBlockSystem();
  private CustomRecipeSystem customRecipeSystem = new CustomRecipeSystem();

  @NModuleTrigger(trigger = "PRE_INITIALIZATION")
  public void preInit() {
    customItemSystem.preInit();

    customBlockSystem.preInit();

    customItemSystem.associate();
    customBlockSystem.associate();

    customRecipeSystem.associate();
  }

  @NModuleTrigger(trigger = "FMLInitializationEvent")
  public void init() {
    customItemSystem.init();
    customBlockSystem.init();
    customSetEffectSystem.init();
  }
}
