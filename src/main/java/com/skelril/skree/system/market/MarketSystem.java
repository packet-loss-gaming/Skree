/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.market;

import com.nearce.gamechatter.sponge.GameChatterPlugin;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.market.MarketCommand;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.MarketServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;

@NModule(name = "Market System")
public class MarketSystem implements ServiceProvider<MarketService> {
    private MarketService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new MarketServiceImpl();

        // Register the service
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), MarketService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), MarketCommand.aquireSpec(), "market", "mk");

        // Schedule an update task for every two hours
        Task.builder().execute(() -> {
            service.updatePrices();

            MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, "The market has been updated"));
            GameChatterPlugin.inst().sendSystemMessage("The market has been updated");
        }).interval(2, TimeUnit.HOURS).async().submit(SkreePlugin.inst());
    }

    @Override
    public MarketService getService() {
        return service;
    }
}
