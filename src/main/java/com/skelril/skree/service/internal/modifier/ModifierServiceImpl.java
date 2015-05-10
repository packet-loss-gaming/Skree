/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier;

import com.skelril.skree.service.ModifierService;

import java.util.HashMap;
import java.util.Map;

public class ModifierServiceImpl implements ModifierService {

    private Map<String, Long> modifiers = new HashMap<>();

    @Override
    public void setExpiry(String modifier, long time) {
        modifiers.put(modifier, time);
    }

    @Override
    public long expiryOf(String modifier) {
        return modifiers.get(modifier);
    }

    @Override
    public Map<String, Long> getActiveModifiers() {
        Map<String, Long> map = new HashMap<>();
        long curTime = System.currentTimeMillis();
        modifiers.entrySet().stream().filter(entry -> entry.getValue() > curTime).forEach(
                entry -> map.put(entry.getKey(), entry.getValue())
        );
        return map;
    }
}
