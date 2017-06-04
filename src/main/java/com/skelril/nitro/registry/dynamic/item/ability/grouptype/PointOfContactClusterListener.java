/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability.grouptype;

import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownHandler;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class PointOfContactClusterListener implements ClusterListener {
  private PointOfContactCluster attackCluster;
  private String itemID;
  private AbilityCooldownHandler cooldownHandler;

  public PointOfContactClusterListener(PointOfContactCluster attackCluster, String itemID, AbilityCooldownHandler cooldownHandler) {
    this.attackCluster = attackCluster;
    this.itemID = itemID;
    this.cooldownHandler = cooldownHandler;
  }

  public boolean isApplicable(Living sourceEntity) {
    if (!(sourceEntity instanceof ArmorEquipable)) {
      return false;
    }

    Optional<ItemStack> optHeldItem = ((ArmorEquipable) sourceEntity).getItemInHand(HandTypes.MAIN_HAND);
    return optHeldItem.isPresent() && optHeldItem.get().getItem().getId().equals(itemID);
  }

  @Listener
  public void onBlockCollide(CollideEvent.Impact event, @First Projectile projectile) {
    ProjectileSource source = projectile.getShooter();
    if (!(source instanceof Living)) {
      return;
    }

    Living sourceEntity = (Living) source;
    if (!isApplicable(sourceEntity)) {
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
