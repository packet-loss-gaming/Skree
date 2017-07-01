/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.projectilewatcher;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ProjectileWatcherService;
import com.skelril.skree.service.internal.projectilewatcher.ProjectileWatcherServiceImpl;
import com.skelril.skree.service.internal.projectilewatcher.shooter.ImmutableShootingItemData;
import com.skelril.skree.service.internal.projectilewatcher.shooter.ShootingItemData;
import com.skelril.skree.service.internal.projectilewatcher.shooter.ShootingItemDataManipulatorBuilder;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;

@NModule(name = "Projectile Watcher System")
public class ProjectileWatcherSystem implements ServiceProvider<ProjectileWatcherService> {
  private ProjectileWatcherService service;

  @NModuleTrigger(trigger = "INITIALIZATION")
  public void init() {
    service = new ProjectileWatcherServiceImpl();

    // Register Shooting Item Manipulator
    DataRegistration.builder()
        .dataClass(ShootingItemData.class)
        .immutableClass(ImmutableShootingItemData.class)
        .builder(new ShootingItemDataManipulatorBuilder())
        .manipulatorId("shooting_item_manipulator")
        .dataName("Shooting Item Data")
        .buildAndRegister(SkreePlugin.container());

    // Register the service & command
    Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
    Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ProjectileWatcherService.class, service);
  }

  @Override
  public ProjectileWatcherService getService() {
    return service;
  }
}
