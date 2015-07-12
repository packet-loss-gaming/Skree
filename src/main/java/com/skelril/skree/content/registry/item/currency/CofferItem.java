/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.currency;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import org.apache.commons.lang3.Validate;

public class CofferItem extends CustomItem {

    private final String ID;
    private final int cofferValue;

    public CofferItem(String ID, int cofferValue) {
        super();
        Validate.isTrue(cofferValue < 1, "Currency can now be worth less than 1 coffer");
        this.ID = ID.toLowerCase();
        this.cofferValue = cofferValue;
    }

    public int getCofferValue() {
        return cofferValue;
    }

    @Override
    public String __getID() {
        return ID + "_coffer";
    }

    @Override
    public int __getMaxStackSize() {
        return 64;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabMisc;
    }
}
