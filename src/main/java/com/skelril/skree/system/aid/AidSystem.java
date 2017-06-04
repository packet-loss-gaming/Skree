/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.aid;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.aid.AntiPeskListener;
import com.skelril.skree.content.aid.ChatCommandAid;
import com.skelril.skree.system.ConfigLoader;
import org.spongepowered.api.Sponge;

import java.io.IOException;

@NModule(name = "Aid System")
public class AidSystem {
  @NModuleTrigger(trigger = "SERVER_STARTED")
  public void init() {
    try {
      AntiPeskConfig config = ConfigLoader.loadConfig("anti_pesk.json", AntiPeskConfig.class);

      Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ChatCommandAid());
      Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new AntiPeskListener(config));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

