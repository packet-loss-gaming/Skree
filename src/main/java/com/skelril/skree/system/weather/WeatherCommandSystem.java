/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.weather;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.weather.WeatherCommand;
import org.spongepowered.api.Sponge;

public class WeatherCommandSystem {
    public WeatherCommandSystem() {
        Sponge.getCommandManager().removeMapping(Sponge.getCommandManager().get("weather").get());
        Sponge.getCommandManager().register(SkreePlugin.inst(), WeatherCommand.aquireSpec(), "weather");
    }
}

