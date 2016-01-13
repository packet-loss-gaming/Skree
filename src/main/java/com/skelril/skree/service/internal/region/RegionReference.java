/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.google.common.collect.Lists;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class RegionReference {
    private final Region ref;
    private final RegionManager manager;

    private RegionPoint min;
    private RegionPoint max;

    private boolean active = false;

    private List<RegionPoint> points = new ArrayList<>();

    protected RegionReference(Region ref, RegionManager manager) {
        this.ref = ref;
        this.manager = manager;

        loadPoints();
    }

    public Region getReferred() {
        return ref;
    }

    public RegionPoint getMax() {
        return max;
    }

    public RegionPoint getMin() {
        return min;
    }

    public List<RegionPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public boolean addPoint(RegionPoint newPoint) {
        return addPoint(Collections.singleton(newPoint));
    }

    public boolean addPoint(Collection<RegionPoint> newPoints) {
        for (RegionPoint point : newPoints) {
            if (manager.createsIntersect(this, point)) {
                return false;
            }
        }
        uncheckedAddPoint(newPoints);
        return true;
    }

    public boolean remPoint(RegionPoint oldPoint) {
        return remPoint(Collections.singleton(oldPoint));
    }

    public boolean remPoint(Collection<RegionPoint> oldPoint) {
        uncheckedRemPoint(oldPoint);
        return true;
    }

    protected void uncheckedAddPoint(Collection<RegionPoint> newPoints) {
        ref.uncheckedAddPoint(newPoints);
        loadPoints();
    }

    protected void uncheckedRemPoint(Collection<RegionPoint> oldPoints) {
        ref.uncheckedRemPoint(oldPoints);
        loadPoints();
    }

    private void loadPoints() {
        points = new ArrayList<>(ref.getFullPoints());
        convexHull(points, true, true);
        checkIsActive();
    }

    public boolean isMarkedPoint(RegionPoint point) {
        return getReferred().getFullPoints().contains(point) || getReferred().getMasterBlock().equals(point);
    }

    public void addMember(UUID newMember) {
        addMember(Collections.singleton(newMember));
    }

    public void addMember(Collection<UUID> newMembers) {
        ref.uncheckedAddMember(newMembers);
    }

    public void remMember(UUID oldMember) {
        remMember(Collections.singleton(oldMember));
    }

    public void remMember(Collection<UUID> oldMembers) {
        ref.uncheckedRemMember(oldMembers);
    }

    public boolean isMember(Player player) {
        return getReferred().getMembers().contains(player.getUniqueId());
    }

    public boolean isEditPrevented(Player player, RegionPoint point) {
        return isActive() && !isMember(player);
    }

    public boolean isActive() {
        return active;
    }

    public boolean contains(RegionPoint pos) {
        return isActive() && uncheckedContains(pos);
    }

//    public boolean possiblyContains(RegionPoint inPos) {
//        TestRegionPoint pos = new TestRegionPoint(inPos);
//        if (quickContains(pos)) {
//            List<RegionPoint> pointsA = Lists.newArrayList(this.points);
//            List<RegionPoint> pointsB = Lists.newArrayList(this.points);
//            pointsB.add(pos);
//
//            convexHull(pointsA, false, false);
//            convexHull(pointsB, false, false);
//
//            return pointsA.size() == pointsB.size();
//        }
//        return false;
//    }

    protected boolean uncheckedContains(RegionPoint pos) {
        if (quickContains(pos)) {
            // Add all the points to a new common point set
            List<RegionPoint> points = Lists.newArrayList(this.points);
            points.add(pos);

            // Perform convex hull
            convexHull(points, false, true);

            // If our point set is equal, the point was within in the hull
            return points.size() == this.points.size();
        }
        return false;
    }

    private boolean quickContains(RegionPoint pos) {
        if (points.size() < 3) {
            return false;
        }

        if (getMin().getX() <= pos.getX() && pos.getX() <= getMax().getX()) {
            if (getMin().getZ() <= pos.getZ() && pos.getZ() <= getMax().getZ()) {
                return true;
            }
        }
        return false;
    }

    private void checkIsActive() {
        active = uncheckedContains(ref.getMasterBlock());
    }

    public double cross(RegionPoint from, RegionPoint through, RegionPoint to) {
        return (through.getX() - from.getX()) * (to.getZ() - from.getZ()) - (through.getZ() - from.getZ()) * (to.getX() - from.getX());
    }

    public void convexHull(List<RegionPoint> regionPoints, boolean asd, boolean sdf) {
        if (regionPoints.size() > 1) {
            int k = 0;
            int n = regionPoints.size();

            List<RegionPoint> hullPoints = Arrays.asList(new RegionPoint[n * 2]);

            regionPoints.sort((a, b) -> {
                if (a.getX() == b.getX()) {
                    return (int) Math.round(a.getZ() - b.getZ());
                } else {
                    return (int) Math.round(a.getX() - b.getX());
                }
            });

            for (RegionPoint point : regionPoints) {
                processPoint(point, asd);
            }

            // Build lower hull
            for (int i = 0; i < n; ++i) {
                while (k >= 2 && cross(hullPoints.get(k - 2), hullPoints.get(k - 1), regionPoints.get(i)) <= 0) {
                    k--;
                }
                hullPoints.set(k++, regionPoints.get(i));
            }

            // Build upper hull
            for (int i = n - 2, t = k + 1; i >= 0; i--) {
                while (k >= t && cross(hullPoints.get(k - 2), hullPoints.get(k - 1), regionPoints.get(i)) <= 0) {
                    k--;
                }
                hullPoints.set(k++, regionPoints.get(i));
            }

            if (k > 1) {
                hullPoints = hullPoints.subList(0, k - 1); // remove non-hull vertices after k; remove k - 1 which is a duplicate
            }

            regionPoints.clear();
            regionPoints.addAll(hullPoints);
        }
    }

    private void processPoint(RegionPoint newPoint, boolean updateMinAndMax) {
        if (!updateMinAndMax) {
            return;
        }

        if (min == null || max == null) {
            min = max = newPoint;
        }

        if (newPoint.getX() < min.getX() || newPoint.getZ() < min.getZ()) {
            min = new RegionPoint(
                    Math.min(min.getX(), newPoint.getX()),
                    Math.min(min.getY(), newPoint.getY()),
                    Math.min(min.getZ(), newPoint.getZ())
            );
        } else if (newPoint.getX() > max.getX() || newPoint.getZ() > max.getZ()) {
            max = new RegionPoint(
                    Math.max(max.getX(), newPoint.getX()),
                    Math.max(max.getY(), newPoint.getY()),
                    Math.max(max.getZ(), newPoint.getZ())
            );
        }
    }


    private double sqrDist(RegionPoint p1, RegionPoint p2) {
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ());
    }

    private enum Turn {
        CLOCKWISE, COUNTER_CLOCKWISE, COLINEAR
    }
}