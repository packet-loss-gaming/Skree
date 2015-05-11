/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice;

import com.skelril.skree.service.internal.market.MarketEntry;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;

import java.math.BigDecimal;

public class FixedPriceMarketEntry implements MarketEntry {

    private final String name;
    private ItemStackBuilder builder;
    private BigDecimal value;
    private float buyPercent;
    private float sellPercent;

    public FixedPriceMarketEntry(String name, ItemStackBuilder builder, BigDecimal value, float buyPercent, float sellPercent) {
        this.name = name;
        this.builder = builder;
        this.value = value;
        this.buyPercent = buyPercent;
        this.sellPercent = sellPercent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack buildItem() {
        return builder.build();
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    protected void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal getValueBoughtFor() {
        return value.multiply(new BigDecimal(buyPercent));
    }

    protected void setBuyPercent(float buyPercent) {
        this.buyPercent = buyPercent;
    }

    @Override
    public BigDecimal getValueSoldFor() {
        return value.multiply(new BigDecimal(sellPercent));
    }

    protected void setSellPercent(float sellPercent) {
        this.sellPercent = sellPercent;
    }
}
