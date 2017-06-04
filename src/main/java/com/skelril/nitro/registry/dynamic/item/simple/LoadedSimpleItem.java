/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.simple;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

class LoadedSimpleItem extends Item {
  private SimpleItemConfig config;

  protected LoadedSimpleItem(SimpleItemConfig config) {
    this.config = config;

    setMaxStackSize();
    setCreativeTab();
  }

  // Config Loading

  private void setMaxStackSize() {
    this.setMaxStackSize(config.getMaxStackSize());
  }

  private void setCreativeTab() {
    for (CreativeTabs creativeTab : CreativeTabs.CREATIVE_TAB_ARRAY) {
      if (creativeTab.tabLabel.equals(config.getCreativeTab())) {
        this.setCreativeTab(creativeTab);
        break;
      }
    }
  }
}
