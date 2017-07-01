/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.Clause;
import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import static com.skelril.skree.service.ProjectileWatcherService.SHOOTING_ITEM_DATA_KEY;

public class RangedSpecialAttackClusterListener implements ClusterListener {
  private RangedSpecialAttackCluster rangedCluster;
  private AbilityApplicabilityTest applicabilityTest;
  private AbilityCooldownHandler cooldownHandler;

  public RangedSpecialAttackClusterListener(RangedSpecialAttackCluster rangedCluster, AbilityApplicabilityTest applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    this.rangedCluster = rangedCluster;
    this.applicabilityTest = applicabilityTest;
    this.cooldownHandler = cooldownHandler;
  }

  public Optional<Clause<Living, ItemStackSnapshot>> getSource(Cause cause) {
    Optional<EntityDamageSource> optEntityDamageSource = cause.first(EntityDamageSource.class);
    if (!optEntityDamageSource.isPresent()) {
      return Optional.empty();
    }

    EntityDamageSource damageSource = optEntityDamageSource.get();
    if (!(damageSource instanceof IndirectEntityDamageSource)) {
      return Optional.empty();
    }

    Entity projectile = damageSource.getSource();
    Entity source = ((IndirectEntityDamageSource) damageSource).getIndirectSource();
    if (!(source instanceof Living)) {
      return Optional.empty();
    }

    return Optional.of(new Clause<>((Living) source, projectile.get(SHOOTING_ITEM_DATA_KEY).map(Optional::get).orElse(null)));
  }

  @Listener(order = Order.LATE)
  public void onPlayerCombat(DamageEntityEvent event) {
    Entity targetEntity = event.getTargetEntity();
    if (!(targetEntity instanceof Living)) {
      return;
    }

    Optional<Clause<Living, ItemStackSnapshot>> optSourceEntity = getSource(event.getCause());
    if (!optSourceEntity.isPresent()) {
      return;
    }

    Living sourceEntity = optSourceEntity.get().getKey();
    ItemStackSnapshot snapshot = optSourceEntity.get().getValue();
    if (!applicabilityTest.test(sourceEntity, snapshot)) {
      return;
    }

    if (cooldownHandler.canUseAbility(sourceEntity)) {
      cooldownHandler.useAbility(sourceEntity);
    } else {
      return;
    }

    rangedCluster.getNextAttackToRun().run(sourceEntity, (Living) targetEntity, event);
  }
}
