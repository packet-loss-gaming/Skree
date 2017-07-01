/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import static com.skelril.skree.service.ProjectileWatcherService.SHOOTING_ITEM_DATA_KEY;

public class PointOfContactClusterListener implements ClusterListener {
  private PointOfContactCluster attackCluster;
  private AbilityApplicabilityTest applicabilityTest;
  private AbilityCooldownHandler cooldownHandler;

  public PointOfContactClusterListener(PointOfContactCluster attackCluster, AbilityApplicabilityTest applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    this.attackCluster = attackCluster;
    this.applicabilityTest = applicabilityTest;
    this.cooldownHandler = cooldownHandler;
  }

  @Listener
  public void onBlockCollide(CollideEvent.Impact event, @First Projectile projectile) {
    ProjectileSource source = projectile.getShooter();
    if (!(source instanceof Living)) {
      return;
    }

    Living sourceEntity = (Living) source;
    ItemStackSnapshot shootingItemStack = projectile.get(SHOOTING_ITEM_DATA_KEY).map(Optional::get).orElse(null);
    if (!applicabilityTest.test(sourceEntity, shootingItemStack)) {
      return;
    }

    if (cooldownHandler.canUseAbility(sourceEntity)) {
      cooldownHandler.useAbility(sourceEntity);
    } else {
      return;
    }

    attackCluster.getNextAbilityToRun().run(sourceEntity, event.getImpactPoint());
  }
}
