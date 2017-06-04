/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.itemrestriction;

import java.util.HashSet;
import java.util.Set;

public class ItemRestrictionConfig {
  private Set<String> blockedItems = new HashSet<>();

  public Set<String> getBlockedItems() {
    return blockedItems;
  }
}
