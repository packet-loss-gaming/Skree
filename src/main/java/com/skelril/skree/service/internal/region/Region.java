/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import java.util.*;

public class Region {
    private final UUID regionID;
    private final String worldName;
    private final RegionPoint masterBlock;
    private int powerLevel = 200;
    private Set<RegionPoint> fullPoints = new HashSet<>();

    public Region(UUID regionID, String worldName, RegionPoint masterBlock) {
        this.regionID = regionID;
        this.worldName = worldName;
        this.masterBlock = masterBlock;
    }

    public UUID getID() {
        return regionID;
    }

    public String getWorldName() {
        return worldName;
    }

    public RegionPoint getMasterBlock() {
        return masterBlock;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public Set<RegionPoint> getFullPoints() {
        return Collections.unmodifiableSet(fullPoints);
    }

    protected void uncheckedAddPoint(Collection<RegionPoint> newPoints) {
        writeAdditionToDB(newPoints);
        fullPoints.addAll(newPoints);
    }

    protected void uncheckedRemPoint(Collection<RegionPoint> oldPoints) {
        writeRemovalFromDB(oldPoints);
        fullPoints.removeAll(oldPoints);
    }

    private void writeAdditionToDB(Collection<RegionPoint> point) {

    }

    private void writeRemovalFromDB(Collection<RegionPoint> point) {

    }
}
