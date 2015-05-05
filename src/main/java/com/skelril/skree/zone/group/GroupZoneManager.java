/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.zone.group;

import com.skelril.skree.zone.Zone;
import com.skelril.skree.zone.ZoneManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GroupZoneManager<T extends Zone> implements ZoneManager<T> {
    protected List<T> zones = new ArrayList<>();

    @Override
    public Collection<T> getActiveZones() {
        return new ArrayList<>(zones);
    }
}
