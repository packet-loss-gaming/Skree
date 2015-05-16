/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Collection;

public interface MarketEntry {
    String getName();
    Collection<String> getAliases();

    ItemStack buildItem();

    BigDecimal getValue();

    /**
     * @return the price the system will instantly buy the entry from you for
     */
    BigDecimal getValueBoughtFor();
    /**
     * @return the price the system will instantly sell the entry to you for
     */
    BigDecimal getValueSoldFor();
}
