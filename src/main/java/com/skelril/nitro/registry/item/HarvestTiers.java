/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.skelril.nitro.registry.HarvestTier;

public final class HarvestTiers {
    public static final HarvestTier CRYSTAL = new HarvestTier("Crystal", 4);
    public static final HarvestTier DIAMOND = new HarvestTier("Diamond", 3);
    public static final HarvestTier IRON = new HarvestTier("Diamond", 2);
    public static final HarvestTier STONE = new HarvestTier("Diamond", 1);
    public static final HarvestTier WOOD = new HarvestTier("Diamond", 0);

    private HarvestTiers() { }
}
