/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.highscore;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.highscore.HighScoreCommand;
import com.skelril.skree.service.HighScoreService;
import com.skelril.skree.service.internal.highscore.HighScoreServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "HighScore System")
public class HighScoreSystem implements ServiceProvider<HighScoreService> {
  private HighScoreService service;

  @NModuleTrigger(trigger = "PRE_INITIALIZATION")
  public void init() {
    service = new HighScoreServiceImpl();
    Sponge.getServiceManager().setProvider(SkreePlugin.inst(), HighScoreService.class, service);
    Sponge.getCommandManager().register(SkreePlugin.inst(), HighScoreCommand.aquireSpec(), "highscores", "highscore");
  }

  @Override
  public HighScoreService getService() {
    return service;
  }
}
