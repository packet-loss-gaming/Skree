/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.melee;

import com.google.common.base.Optional;
import com.skelril.skree.content.registry.charm.AbstractCharm;
import com.skelril.skree.content.registry.charm.CharmTools;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntityInteractEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class LifeLeachCharm extends AbstractCharm {
    public LifeLeachCharm() {
        super(0, "life_leech", 3);
    }

    @Subscribe
    public void onEntityInteract(EntityInteractEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ArmorEquipable) {
            Optional<ItemStack> held = ((ArmorEquipable) entity).getItemInHand();
            if (held.isPresent()) {
                int level = CharmTools.getLevel(held.get(), this);
                if (level > 0) {
                    entity
                }
            }
        }
    }
}
