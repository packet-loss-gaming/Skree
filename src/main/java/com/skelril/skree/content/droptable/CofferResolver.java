/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.droptable;

import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.resolver.point.SimplePointDropResolver;
import com.skelril.nitro.droptable.resolver.point.SimplePointValue;
import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import org.spongepowered.api.item.ItemType;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.item.CustomItemTypes.*;

public class CofferResolver extends SimplePointDropResolver {
    public CofferResolver(int maxCoffers) {
        this(maxCoffers, ModifierFunctions.MULTI);
    }

    public CofferResolver(int maxCoffers, ModifierFunction modifierFunction) {
        super(
                Lists.newArrayList(
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) TESTRIL)),
                                TESTRIL.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) AQUIS)),
                                AQUIS.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) MARSINCO)),
                                MARSINCO.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) POSTRE)),
                                POSTRE.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) EQESTA)),
                                EQESTA.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) REDISTRAL)),
                                REDISTRAL.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) RETESRUM)),
                                RETESRUM.getCofferValue()
                        ),
                        new SimplePointValue(
                                Lists.newArrayList(newItemStack((ItemType) MESARDITH)),
                                MESARDITH.getCofferValue()
                        )
                ),
                maxCoffers,
                modifierFunction
        );
    }
}
