/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.google.gson.GsonBuilder;
import com.skelril.nitro.registry.dynamic.Loader;
import com.skelril.nitro.registry.dynamic.ability.AbilityEnabledGsonBuilder;
import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import net.minecraft.item.Item;

public abstract class ItemLoader<ResultType extends Item, ConfigType extends ItemConfig> implements Loader<ConfigType> {
  private GameIntegrator integrator;
  private AbilityRegistry abilityRegistry;

  public ItemLoader(GameIntegrator integrator, AbilityRegistry abilityRegistry) {
    this.integrator = integrator;
    this.abilityRegistry = abilityRegistry;
  }

  public abstract ResultType constructFromConfig(ConfigType config);

  @Override
  public GsonBuilder getGsonBuilder() {
    GsonBuilder parentBuilder = Loader.super.getGsonBuilder();

    return AbilityEnabledGsonBuilder.getGsonBuilder(parentBuilder, abilityRegistry);
  }

  @Override
  public void load(ConfigType config) {
    registerWithGame(constructFromConfig(config), config);
  }

  protected void registerWithGame(Item item, ItemConfig config) {
    integrator.registerForProcessing(item, config);
  }
}
