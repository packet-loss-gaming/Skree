/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.respawnqueue;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.RespawnQueueService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.NonNullList;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.*;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class RespawnQueueServiceImpl implements RespawnQueueService {

    private HashMap<UUID, Deque<ItemStack>> playerQueue = new HashMap<>();

    @Override
    public void enque(Player player, Collection<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }

        Deque<ItemStack> queue = new ArrayDeque<>();
        queue.addAll(stacks);

        playerQueue.merge(player.getUniqueId(), queue, (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event) {
        EntityPlayer player = tf(event.getTargetEntity());
        NonNullList<net.minecraft.item.ItemStack> mainInv = player.inventory.mainInventory;
        Task.builder().delayTicks(1).execute(() -> {
            Deque<ItemStack> queue = playerQueue.remove(player.getUniqueID());
            if (queue != null) {
                for (int i = 0; !queue.isEmpty() && i < mainInv.size(); ++i) {
                    if (mainInv.get(i) == net.minecraft.item.ItemStack.EMPTY) {
                        mainInv.set(i, tf(queue.poll()));
                    }
                }
                if (!queue.isEmpty()) {
                    enque(tf(player), queue);
                }
            }
        }).submit(SkreePlugin.inst());
    }
}
