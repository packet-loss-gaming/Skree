/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.openboss.EntityDetail;

public class WildernessBossDetail implements EntityDetail {
    private int level;

    public WildernessBossDetail(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
