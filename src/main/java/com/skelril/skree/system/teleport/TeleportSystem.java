/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.teleport;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.teleport.BringCommand;
import com.skelril.skree.content.teleport.TeleportCommand;
import org.spongepowered.api.Sponge;

@NModule(name = "Teleport System")
public class TeleportSystem {
  @NModuleTrigger(trigger = "SERVER_STARTED")
  public void init() {
    Sponge.getCommandManager().removeMapping(Sponge.getCommandManager().get("tp").get());
    Sponge.getCommandManager().register(SkreePlugin.inst(), TeleportCommand.aquireSpec(), "teleport", "tp");
    Sponge.getCommandManager().register(SkreePlugin.inst(), BringCommand.aquireSpec(), "bring", "br");
  }
}
