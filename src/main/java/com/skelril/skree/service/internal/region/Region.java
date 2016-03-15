/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Region {
    UUID getID();

    String getWorldName();

    RegionPoint getMasterBlock();

    String getName();

    int getPowerLevel();

    double getArea();
    double getMaximumArea();

    Set<UUID> getMembers();

    Set<RegionPoint> getFullPoints();

    RegionPoint getMax();

    RegionPoint getMin();

    List<RegionPoint> getPoints();

    RegionErrorStatus addPoint(RegionPoint newPoint);

    RegionErrorStatus addPoint(Collection<RegionPoint> newPoints);

    RegionErrorStatus remPoint(RegionPoint oldPoint);

    RegionErrorStatus remPoint(Collection<RegionPoint> oldPoint);

    boolean isMarkedPoint(RegionPoint point);

    void addMember(UUID newMember);

    void addMember(Collection<UUID> newMembers);

    void remMember(UUID oldMember);

    void remMember(Collection<UUID> oldMembers);

    boolean isMember(Player player);

    boolean isEditPrevented(Player player, RegionPoint point);

    boolean isActive();

    boolean contains(RegionPoint pos);
}
