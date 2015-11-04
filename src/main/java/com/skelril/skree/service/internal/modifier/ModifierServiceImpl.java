/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier;

import com.skelril.skree.service.ModifierService;

import java.util.HashMap;
import java.util.Map;

public abstract class ModifierServiceImpl implements ModifierService {

    protected final Map<String, Long> modifiers = new HashMap<>();

    public ModifierServiceImpl() {
        refreshData();
    }

    @Override
    public void setExpiry(String modifier, long time) {
        modifiers.put(modifier, time);
        updateModifier(modifier, time);
    }

    public void refreshData() {
        modifiers.clear();
        repopulateData();
    }

    protected abstract void repopulateData();

    protected abstract void updateModifier(String modifier, long time);

    @Override
    public long expiryOf(String modifier) {
        Long mod = modifiers.get(modifier);
        return mod == null ? 0 : mod;
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
