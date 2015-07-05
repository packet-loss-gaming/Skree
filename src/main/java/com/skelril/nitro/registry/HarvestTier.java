/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry;

public class HarvestTier {

    private String tierName;
    private int translation;

    public HarvestTier(String tierName, int translation) {
        this.tierName = tierName;
        this.translation = translation;
    }

    public String getTierName() {
        return tierName;
    }

    public int getTranslation() {
        return translation;
    }
}
