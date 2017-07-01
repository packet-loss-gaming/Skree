/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class MeleeAttackClusterListener implements ClusterListener {
  private MeleeAttackCluster attackCluster;
  private AbilityApplicabilityTest applicabilityTest;
  private AbilityCooldownHandler cooldownHandler;

  public MeleeAttackClusterListener(MeleeAttackCluster attackCluster, AbilityApplicabilityTest applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    this.attackCluster = attackCluster;
    this.applicabilityTest = applicabilityTest;
    this.cooldownHandler = cooldownHandler;
  }

  public Optional<Living> getSource(Cause cause) {
    Optional<EntityDamageSource> optEntityDamageSource = cause.first(EntityDamageSource.class);
    if (!optEntityDamageSource.isPresent()) {
      return Optional.empty();
    }

    EntityDamageSource damageSource = optEntityDamageSource.get();
    Entity source = damageSource.getSource();
    if (!(source instanceof Living)) {
      return Optional.empty();
    }

    return Optional.of((Living) source);
  }

  private boolean test(Living sourceEntity) {
    if (!(sourceEntity instanceof ArmorEquipable)) {
      return applicabilityTest.test(sourceEntity, null);
    }

    return applicabilityTest.test(sourceEntity, ((ArmorEquipable) sourceEntity).getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::createSnapshot).orElse(null));
  }

  @Listener(order = Order.LATE)
  public void onPlayerCombat(DamageEntityEvent event) {
    Entity targetEntity = event.getTargetEntity();
    if (!(targetEntity instanceof Living)) {
      return;
    }

    Optional<Living> optSourceEntity = getSource(event.getCause());
    if (!optSourceEntity.isPresent()) {
      return;
    }

    Living sourceEntity = optSourceEntity.get();
    if (!test(sourceEntity)) {
      return;
    }

    if (cooldownHandler.canUseAbility(sourceEntity)) {
      cooldownHandler.useAbility(sourceEntity);
    } else {
      return;
    }

    attackCluster.getNextAttackToRun().run(sourceEntity, (Living) targetEntity, event);
  }
}
