/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.random;


import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.random.HeartsCommand;
import org.spongepowered.api.Sponge;

@NModule(name = "Random System")
public class RandomSystem {
  @NModuleTrigger(trigger = "SERVER_STARTED", dependencies = {"World System"})
  public void init() {
    // Register the service & command
    Sponge.getCommandManager().register(SkreePlugin.inst(), HeartsCommand.aquireSpec(), "hearts");
  }
}
