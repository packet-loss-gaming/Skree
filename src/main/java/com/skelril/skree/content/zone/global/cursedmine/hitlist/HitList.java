/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.hitlist;

import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HitList {

    private Map<UUID, Long> hitList = new HashMap<>();

    public void addPlayer(Player player) {
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
        hitList.put(player.getUniqueId(), expiryTime);
    }

    public void remPlayer(Player player) {
        hitList.remove(player.getUniqueId());
    }

    public boolean isOnHitList(Player player) {
        return hitList.containsKey(player.getUniqueId());
    }

    public void check() {
        Iterator<Map.Entry<UUID, Long>> it = hitList.entrySet().iterator();
        while (it.hasNext()) {
            if (hasExpired(it.next().getValue())) it.remove();
        }
    }

    private boolean hasExpired(long expiryTime) {
        return expiryTime < System.currentTimeMillis();
    }
}