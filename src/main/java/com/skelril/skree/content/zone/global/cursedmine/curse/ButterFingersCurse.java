/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import com.skelril.nitro.item.ItemDropper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ButterFingersCurse implements Consumer<Player> {
    @Override
    public void accept(Player player) {
        List<ItemStack> drops = new ArrayList<>();
        while (true) {
            Optional<ItemStack> optDrop = player.getInventory().poll();
            if (!optDrop.isPresent()) {
                break;
            }
            drops.add(optDrop.get());
        }

        Collections.shuffle(drops);

        new ItemDropper(player.getLocation()).dropStacks(drops, SpawnTypes.DROPPED_ITEM);
    }
}
