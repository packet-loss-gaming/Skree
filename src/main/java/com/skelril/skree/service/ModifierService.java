/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;

import java.util.Collection;

public interface ModifierService {
    void setExpiry(String modifierName, long time);
    long expiryOf(String modifierName);
    default long statusOf(String modifierName) {
        return Math.max(expiryOf(modifierName) - System.currentTimeMillis(), 0);
    }
    default boolean isActive(String modifierName) {
        return statusOf(modifierName) != 0;
    }

    Collection<Clause<String, Long>> getActiveModifiers();
}
