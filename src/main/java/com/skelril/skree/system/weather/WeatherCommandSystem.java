/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.weather;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.weather.WeatherCommand;
import org.spongepowered.api.Sponge;

@NModule(name = "Weather Command System")
public class WeatherCommandSystem {
    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        Sponge.getCommandManager().removeMapping(Sponge.getCommandManager().get("weather").get());
        Sponge.getCommandManager().register(SkreePlugin.inst(), WeatherCommand.aquireSpec(), "weather");
    }
}

