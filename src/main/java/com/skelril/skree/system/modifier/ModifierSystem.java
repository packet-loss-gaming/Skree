/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.modifier;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.modifier.ModExtendCommand;
import com.skelril.skree.content.modifier.ModifierNotifier;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.modifier.LazyMySQLModifierService;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Modifier System")
public class ModifierSystem implements ServiceProvider<ModifierService> {

  private ModifierService service;

  @NModuleTrigger(trigger = "SERVER_STARTED")
  public void init() {
    service = new LazyMySQLModifierService();

    // Register the service
    Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ModifierNotifier());
    Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ModifierService.class, service);
    Sponge.getCommandManager().register(SkreePlugin.inst(), ModExtendCommand.aquireSpec(), "modextend");
  }

  @Override
  public ModifierService getService() {
    return service;
  }
}
