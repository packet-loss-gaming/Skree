/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public interface RespawnQueueService {
    default void enque(Player player, ItemStack stack) {
        enque(player, Collections.singleton(stack));
    }
    void enque(Player player, Collection<ItemStack> stacks);
}
