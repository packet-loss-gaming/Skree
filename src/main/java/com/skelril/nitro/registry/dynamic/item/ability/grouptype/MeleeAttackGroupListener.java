/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability.grouptype;

import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownManager;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class MeleeAttackGroupListener implements GroupListener {
    private MeleeAttackGroup attackGroup;
    private String itemID;
    private AbilityCooldownManager coolDownManager;

    public MeleeAttackGroupListener(MeleeAttackGroup attackGroup, String itemID, AbilityCooldownManager coolDownManager) {
        this.attackGroup = attackGroup;
        this.itemID = itemID;
        this.coolDownManager = coolDownManager;
    }

    public Optional<Living> getSource(Cause cause) {
        Optional<DamageSource> optDamageSource = cause.first(DamageSource.class);
        if (!optDamageSource.isPresent()) {
            return Optional.empty();
        }

        DamageSource damageSource = optDamageSource.get();
        if (!(damageSource instanceof EntityDamageSource)) {
            return Optional.empty();
        }

        Entity source = ((EntityDamageSource) damageSource).getSource();
        if (!(source instanceof Living)) {
            return Optional.empty();
        }

        return Optional.of((Living) source);
    }

    public boolean isApplicable(Living sourceEntity) {
        if (!(sourceEntity instanceof ArmorEquipable)) {
            return false;
        }

        Optional<ItemStack> optHeldItem = ((ArmorEquipable) sourceEntity).getItemInHand(HandTypes.MAIN_HAND);
        return optHeldItem.isPresent() && optHeldItem.get().getItem().getId().equals(itemID);
    }

    @Listener
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
        if (!isApplicable(sourceEntity)) {
            return;
        }

        if (sourceEntity instanceof Player && coolDownManager.canUseAbility((Player) sourceEntity, attackGroup.getCoolDown())) {
            coolDownManager.usedAbility((Player) sourceEntity, attackGroup.getCoolDown());
        } else {
            return;
        }

        attackGroup.getNextAttackToRun().run(sourceEntity, (Living) targetEntity);
    }
}
