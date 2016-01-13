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
    private int powerLevel;
    private Set<UUID> members = new HashSet<>();
    private Set<RegionPoint> fullPoints;

    protected Region(UUID regionID, String worldName, RegionPoint masterBlock, Set<UUID> members) {
        this(regionID, worldName, masterBlock, 200, members, new HashSet<>());
    }

    protected Region(UUID regionID, String worldName, RegionPoint masterBlock, int powerLevel, Set<UUID> members, Set<RegionPoint> fullPoints) {
        this.regionID = regionID;
        this.worldName = worldName;
        this.masterBlock = masterBlock;
        this.powerLevel = powerLevel;
        this.members = members;
        this.fullPoints = fullPoints;
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

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    protected void uncheckedAddMember(Collection<UUID> newMembers) {
        writeMemberAdditionToDB(newMembers);
        members.addAll(newMembers);
    }

    protected void uncheckedRemMember(Collection<UUID> oldMembers) {
        writeMemberRemovalFromDB(oldMembers);
        members.addAll(oldMembers);
    }

    private void writeMemberAdditionToDB(Collection<UUID> newMembers) {

    }

    private void writeMemberRemovalFromDB(Collection<UUID> oldMembers) {

    }

    public Set<RegionPoint> getFullPoints() {
        return Collections.unmodifiableSet(fullPoints);
    }

    protected void uncheckedAddPoint(Collection<RegionPoint> newPoints) {
        writePointAdditionToDB(newPoints);
        fullPoints.addAll(newPoints);
    }

    protected void uncheckedRemPoint(Collection<RegionPoint> oldPoints) {
        writePointRemovalFromDB(oldPoints);
        fullPoints.removeAll(oldPoints);
    }

    private void writePointAdditionToDB(Collection<RegionPoint> point) {

    }

    private void writePointRemovalFromDB(Collection<RegionPoint> point) {

    }
}
