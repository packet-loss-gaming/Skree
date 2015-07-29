/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.teleport;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.teleport.BringCommand;
import com.skelril.skree.content.teleport.TeleportCommand;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.command.CommandService;

public class TeleportSystem {
    public TeleportSystem(SkreePlugin plugin, Game game) {
        CommandService cmdDispatcher = game.getCommandDispatcher();

        cmdDispatcher.removeMapping(cmdDispatcher.get("tp").get());
        cmdDispatcher.register(plugin, TeleportCommand.aquireSpec(game), "teleport", "tp");
        cmdDispatcher.register(plugin, BringCommand.aquireSpec(game), "bring", "br");
    }
}
