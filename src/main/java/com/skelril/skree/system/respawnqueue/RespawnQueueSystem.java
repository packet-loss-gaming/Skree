/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.respawnqueue;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.RespawnQueueService;
import com.skelril.skree.service.internal.respawnqueue.RespawnQueueServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;

@NModule(name = "Respawn Queue System")
public class RespawnQueueSystem implements ServiceProvider<RespawnQueueService> {
  private RespawnQueueService service;

  @NModuleTrigger(trigger = "SERVER_STARTED")
  public void init() {
    service = new RespawnQueueServiceImpl();

    // Register the service
    Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
    Sponge.getServiceManager().setProvider(SkreePlugin.inst(), RespawnQueueService.class, service);
  }

  @Override
  public RespawnQueueService getService() {
    return service;
  }
}
