/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.melee;

import com.skelril.skree.content.registry.charm.AbstractCharm;
import com.skelril.skree.content.registry.charm.CharmTools;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class LifeLeachCharm extends AbstractCharm {
    public LifeLeachCharm() {
        super(0, "life_leech", 3);
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent event) {
        Entity target = event.getTargetEntity();
        Optional<ArmorEquipable> optHolder = event.getCause().first(ArmorEquipable.class);
        if (optHolder.isPresent() && target instanceof Living) {
            ArmorEquipable holder = optHolder.get();
            Optional<ItemStack> optHeldItem = holder.getItemInHand();
            if (optHeldItem.isPresent() && holder instanceof Living) {
                ItemStack heldItem = optHeldItem.get();
                Optional<Integer> optCharmLevel = CharmTools.getLevel(heldItem, this);
                if (optCharmLevel.isPresent()) {
                    int charmLevel = optCharmLevel.get();
                    process((Living) holder, (Living) target, heldItem, charmLevel);
                }
            }
        }
    }

    private void process(Living attacker, Living defender, ItemStack stack, int power) {
        defender.damage(power, Cause.of(this, stack, attacker));
        HealthData data = attacker.getHealthData();
        attacker.offer(Keys.HEALTH, Math.max(data.health().get() + power, data.maxHealth().get()));
    }
}
