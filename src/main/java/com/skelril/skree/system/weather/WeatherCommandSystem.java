/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.weather;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.weather.WeatherCommand;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandManager;

public class WeatherCommandSystem {
    public WeatherCommandSystem(SkreePlugin plugin, Game game) {
        CommandManager cmdDispatcher = game.getCommandManager();

        cmdDispatcher.removeMapping(cmdDispatcher.get("weather").get());
        cmdDispatcher.register(plugin, WeatherCommand.aquireSpec(game), "weather");
    }
}

