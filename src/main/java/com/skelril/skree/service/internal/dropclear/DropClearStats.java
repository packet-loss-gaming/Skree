/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import org.spongepowered.api.entity.EntityType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DropClearStats {
    protected Map<EntityType, Integer> counterQuantity = new HashMap<>();

    public void increase(EntityType type, int amt) {
        Integer count = counterQuantity.get(type);
        if (count != null) {
            count += amt;
        } else {
            count = amt;
        }
        counterQuantity.put(type, count);
    }

    public int total() {
        int total = 0;
        for (Integer i : counterQuantity.values()) {
            total += i;
        }
        return total;
    }

    public Map<EntityType, Integer> getStats() {
        return Collections.unmodifiableMap(counterQuantity);
    }
}
