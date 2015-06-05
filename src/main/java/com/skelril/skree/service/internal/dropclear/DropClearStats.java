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
import java.util.stream.Collectors;

public abstract class DropClearStats {
    protected Map<EntityType, Integer> counterQuantity = new HashMap<>();

    public DropClearStats merge(DropClearStats stats) {
        stats.getStats().entrySet().stream().forEach(entry -> increase(entry.getKey(), entry.getValue()));
        return this;
    }

    public void increase(EntityType type, int amt) {
        counterQuantity.merge(type, amt, (a, b) -> a + b);
    }

    public int total() {
        return counterQuantity.values().stream().collect(Collectors.summingInt((a) -> a));
    }

    public Map<EntityType, Integer> getStats() {
        return Collections.unmodifiableMap(counterQuantity);
    }

    public abstract String getFriendlyIdentifier();
}
