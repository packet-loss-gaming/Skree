/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.droptable;

import com.skelril.nitro.droptable.resolver.point.SimplePointDropResolver;
import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;

import java.math.BigDecimal;

public class CofferResolver extends SimplePointDropResolver<BigDecimal> {
    public CofferResolver(int maxCoffers) {
        this(maxCoffers, ModifierFunctions.MULTI);
    }

    public CofferResolver(int maxCoffers, ModifierFunction modifierFunction) {
        super(CofferValueMap.inst(), BigDecimal::new, maxCoffers, modifierFunction);
    }
}
