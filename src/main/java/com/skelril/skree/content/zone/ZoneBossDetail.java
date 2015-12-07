/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.openboss.EntityDetail;
import com.skelril.skree.service.internal.zone.Zone;

public class ZoneBossDetail<T extends Zone> implements EntityDetail {
    public final T zone;

    public ZoneBossDetail(T zone) {
        this.zone = zone;
    }

    public T getZone() {
        return zone;
    }
}