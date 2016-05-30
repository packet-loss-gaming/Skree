/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.sword;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.item.sword.CustomSword;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

public class TwoTailedSword extends CustomSword implements EventAwareContent {
    @Override
    public int __getMaxUses() {
        return 7;
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return null;
    }

    @Override
    public double __getHitPower() {
        return 0;
    }

    @Override
    public int __getEnchantability() {
        return 0;
    }

    @Override
    public String __getType() {
        return "two_tailed";
    }

    @Listener(order = Order.LATE)
    public void onPlayerCombat(DamageEntityEvent event) {
        new PlayerCombatParser() {
            @Override
            public boolean verify(Living living) {
                if (!(living instanceof ArmorEquipable)) {
                    return false;
                }

                Optional<org.spongepowered.api.item.inventory.ItemStack> optHeld = ((ArmorEquipable) living).getItemInHand();
                if (optHeld.isPresent() && optHeld.get().getItem() == CustomItemTypes.TWO_TAILED_SWORD) {
                    int diff = (int) (living.get(Keys.MAX_HEALTH).orElse(20D) - living.get(Keys.HEALTH).orElse(20D));
                    event.setBaseDamage(Math.max(5, diff * 2));
                    return true;
                }
                return false;
            }

            @Override
            public void processPvP(Player attacker, Player defender) {
                Living target = defender;
                if (Probability.getChance(2)) {
                    target = attacker;
                }
                Optional<Double> optHealth = target.get(Keys.HEALTH);
                if (optHealth.isPresent()) {
                    target.offer(Keys.HEALTH, Math.max(0, optHealth.get() - 16));
                }
            }
        }.parse(event);
    }
}
