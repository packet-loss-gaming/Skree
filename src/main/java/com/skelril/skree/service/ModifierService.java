/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import java.util.Map;

public interface ModifierService {
  void setExpiry(String modifier, long time);

  default void extend(String modifier, long time) {
    setExpiry(modifier, System.currentTimeMillis() + statusOf(modifier) + time);
  }

  long expiryOf(String modifier);

  default long statusOf(String modifier) {
    return Math.max(expiryOf(modifier) - System.currentTimeMillis(), 0);
  }

  default boolean isActive(String modifier) {
    return statusOf(modifier) != 0;
  }

  Map<String, Long> getActiveModifiers();
}
