/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.market;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.market.MarketCommand;
import com.skelril.skree.content.modifier.ModifierNotifier;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.MarketServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

public class MarketSystem implements ServiceProvider<MarketService> {
    private MarketService service;

    @Inject
    public MarketSystem() {
        service = new MarketServiceImpl();

        // Register the service
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ModifierNotifier());
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), MarketService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), MarketCommand.aquireSpec(), "market", "mk");
    }

    @Override
    public MarketService getService() {
        return service;
    }
}
