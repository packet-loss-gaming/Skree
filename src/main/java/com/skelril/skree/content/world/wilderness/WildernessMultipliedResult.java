/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.registry.dynamic.QuantityBoundedItemStackConfig;
import org.spongepowered.api.item.inventory.ItemStack;

public class WildernessMultipliedResult {
    private boolean allowsFortuneMultiplication = true;
    private QuantityBoundedItemStackConfig normal;
    private QuantityBoundedItemStackConfig silkTouch;

    public boolean allowsFortuneMultiplication() {
        return allowsFortuneMultiplication;
    }

    public ItemStack getApplicableResult(boolean silkTouchUsed) {
        if (silkTouchUsed && silkTouch != null) {
            return (ItemStack) (Object) silkTouch.toNSMStack();
        }

        return (ItemStack) (Object) normal.toNSMStack();
    }
}
