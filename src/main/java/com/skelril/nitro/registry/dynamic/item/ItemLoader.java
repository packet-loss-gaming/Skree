/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import net.minecraft.item.Item;

public abstract class ItemLoader {
    private GameIntegrator integrator;

    public ItemLoader(GameIntegrator integrator) {
        this.integrator = integrator;
    }

    protected void registerWithGame(Item item, ItemConfig config) {
        integrator.registerForProcessing(item, config);
    }
}
