/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.currency;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.skelril.nitro.point.ItemStackBigIntegerValueMapping;
import com.skelril.nitro.point.PointValue;
import com.skelril.nitro.point.SimplePointValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigInteger;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.item.CustomItemTypes.*;

public class CofferValueMap extends ItemStackBigIntegerValueMapping {
    public static final ImmutableList<PointValue<ItemStack, BigInteger>> COFFER_VALUE_MAP = ImmutableList.of(
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) TESTRIL)),
                    BigInteger.valueOf(TESTRIL.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) AQUIS)),
                    BigInteger.valueOf(AQUIS.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) MARSINCO)),
                    BigInteger.valueOf(MARSINCO.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) POSTRE)),
                    BigInteger.valueOf(POSTRE.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) EQESTA)),
                    BigInteger.valueOf(EQESTA.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) REDISTRAL)),
                    BigInteger.valueOf(REDISTRAL.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) RETESRUM)),
                    BigInteger.valueOf(RETESRUM.getCofferValue())
            ),
            new SimplePointValue<>(
                    Lists.newArrayList(newItemStack((ItemType) MESARDITH)),
                    BigInteger.valueOf(MESARDITH.getCofferValue())
            )
    );


    private static final CofferValueMap map = new CofferValueMap();

    protected CofferValueMap() {
        super(COFFER_VALUE_MAP);
    }

    public static CofferValueMap inst() {
        return map;
    }
}
