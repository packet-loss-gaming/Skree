/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.currency;

public class CondensedCofferItem extends CofferItem {

  private CofferItem parent;

  public CondensedCofferItem(String id, CofferItem parent) {
    super(id, parent.getCofferValue() * 9);
    this.parent = parent;
  }

  public CofferItem getParent() {
    return parent;
  }
}
