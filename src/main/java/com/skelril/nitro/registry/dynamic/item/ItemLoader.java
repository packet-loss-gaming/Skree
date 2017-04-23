/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.skelril.nitro.registry.dynamic.Loader;
import net.minecraft.item.Item;

public abstract class ItemLoader<ResultType extends Item, ConfigType extends ItemConfig> implements Loader<ConfigType> {
    private GameIntegrator integrator;

    public ItemLoader(GameIntegrator integrator) {
        this.integrator = integrator;
    }

    public abstract ResultType constructFromConfig(ConfigType config);

    @Override
    public void load(ConfigType config) {
        registerWithGame(constructFromConfig(config), config);
    }

    protected void registerWithGame(Item item, ItemConfig config) {
        integrator.registerForProcessing(item, config);
    }
}
