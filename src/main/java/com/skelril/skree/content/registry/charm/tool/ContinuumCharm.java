/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.tool;

import com.google.common.base.Optional;
import com.skelril.skree.content.registry.charm.AbstractCharm;
import com.skelril.skree.content.registry.charm.CharmTools;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntityBreakBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class ContinuumCharm extends AbstractCharm {
    public ContinuumCharm() {
        super(1, "continuum", 9);
    }

    @Subscribe
    public void onBlockBreak(EntityBreakBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ArmorEquipable) {
            Optional<ItemStack> held = ((ArmorEquipable) entity).getItemInHand();
            if (held.isPresent()) {
                int level = CharmTools.getLevel(held.get(), this);
                if (level > 0) {

                }
            }
        }
    }
}
