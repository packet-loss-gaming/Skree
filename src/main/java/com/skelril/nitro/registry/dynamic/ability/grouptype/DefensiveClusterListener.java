/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;
import java.util.function.Predicate;

public class DefensiveClusterListener implements ClusterListener {
  private DefensiveCluster defensiveCluster;
  private Predicate<Living> applicabilityTest;
  private AbilityCooldownHandler cooldownHandler;

  public DefensiveClusterListener(DefensiveCluster defensiveCluster, Predicate<Living> applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    this.defensiveCluster = defensiveCluster;
    this.applicabilityTest = applicabilityTest;
    this.cooldownHandler = cooldownHandler;
  }

  public Optional<Living> getSource(Cause cause) {
    Optional<EntityDamageSource> optEntityDamageSource = cause.first(EntityDamageSource.class);
    if (!optEntityDamageSource.isPresent()) {
      return Optional.empty();
    }

    EntityDamageSource damageSource = optEntityDamageSource.get();
    Entity source;
    if (damageSource instanceof IndirectEntityDamageSource) {
      source = ((IndirectEntityDamageSource) damageSource).getIndirectSource();
    } else {
      source = damageSource.getSource();
    }

    if (!(source instanceof Living)) {
      return Optional.empty();
    }

    return Optional.of((Living) source);
  }

  @Listener(order = Order.LATE)
  public void onPlayerCombat(DamageEntityEvent event) {
    Entity defendingEntity = event.getTargetEntity();
    if (!(defendingEntity instanceof Living)) {
      return;
    }

    Optional<Living> optAttackingEntity = getSource(event.getCause());
    if (!optAttackingEntity.isPresent()) {
      return;
    }

    Living attackingEntity = optAttackingEntity.get();
    if (!applicabilityTest.test((Living) defendingEntity)) {
      return;
    }

    if (cooldownHandler.canUseAbility(defendingEntity)) {
      cooldownHandler.useAbility(defendingEntity);
    } else {
      return;
    }

    defensiveCluster.getNextAbilityToUse().run((Living) defendingEntity, attackingEntity, event);
  }
}
