/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.google.common.collect.Lists;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

class CachedRegion implements Region {
    private final RegionDatabaseHandle ref;
    private final RegionManager manager;

    private RegionPoint min;
    private RegionPoint max;

    private double area;
    private boolean active = false;

    private List<RegionPoint> points = new ArrayList<>();

    protected CachedRegion(RegionDatabaseHandle ref, RegionManager manager) {
        this.ref = ref;
        this.manager = manager;

        loadPoints();
    }

    public RegionDatabaseHandle getHandle() {
        return ref;
    }

    @Override
    public UUID getID() {
        return ref.getID();
    }

    @Override
    public String getWorldName() {
        return ref.getWorldName();
    }

    @Override
    public RegionPoint getMasterBlock() {
        return ref.getMasterBlock();
    }

    @Override
    public String getName() {
        return ref.getName();
    }

    @Override
    public double getArea() {
        return area;
    }

    @Override
    public int getPowerLevel() {
        return ref.getPowerLevel();
    }

    @Override
    public double getMaximumArea() {
        return manager.getPowerToAreaConstant() * ref.getPowerLevel();
    }

    @Override
    public Set<UUID> getMembers() {
        return ref.getMembers();
    }

    @Override
    public Set<RegionPoint> getFullPoints() {
        return ref.getFullPoints();
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

    public RegionErrorStatus addPoint(RegionPoint newPoint) {
        return addPoint(Collections.singleton(newPoint));
    }

    public RegionErrorStatus addPoint(Collection<RegionPoint> newPoints) {
        List<RegionPoint> stagedPoints = new ArrayList<>();
        stagedPoints.addAll(newPoints);
        stagedPoints.addAll(points);

        convexHull(stagedPoints, false);

        if (area(stagedPoints) > getMaximumArea()) {
            return RegionErrorStatus.REGION_TOO_LARGE;
        }

        for (RegionPoint point : newPoints) {
            if (manager.createsIntersect(this, point)) {
                return RegionErrorStatus.INTERSECT;
            }
        }

        uncheckedAddPoint(newPoints);
        return RegionErrorStatus.NONE;
    }

    public RegionErrorStatus remPoint(RegionPoint oldPoint) {
        return remPoint(Collections.singleton(oldPoint));
    }

    public RegionErrorStatus remPoint(Collection<RegionPoint> oldPoint) {
        uncheckedRemPoint(oldPoint);
        return RegionErrorStatus.NONE;
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
        convexHull(points, true);
        area = area(points);
        checkIsActive();
    }

    public boolean isMarkedPoint(RegionPoint point) {
        return ref.getFullPoints().contains(point) || ref.getMasterBlock().equals(point);
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
        return ref.getMembers().contains(player.getUniqueId());
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
            convexHull(points, false);

            // If our point set is equal, the point was within in the hull
            return !hullChanged(points, this.points);
        }
        return false;
    }

    private boolean hullChanged(List<RegionPoint> newPoints, List<RegionPoint> originalPoints) {
        if (newPoints.size() != originalPoints.size()) {
            return true;
        }
        for (int i = 0; i < newPoints.size(); ++i) {
            if (newPoints.get(i).getX() != originalPoints.get(i).getX()) {
                return true;
            }
            if (newPoints.get(i).getZ() != originalPoints.get(i).getZ()) {
                return true;
            }
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

    private double cross(RegionPoint from, RegionPoint through, RegionPoint to) {
        return (through.getX() - from.getX()) * (to.getZ() - from.getZ()) - (through.getZ() - from.getZ()) * (to.getX() - from.getX());
    }

    private double area(List<RegionPoint> regionPoints) {
        double area = 0;
        int k = regionPoints.size() - 1;

        for (int i = 0; i < regionPoints.size(); ++i) {
            double xSum = regionPoints.get(k).getX() + regionPoints.get(i).getX();
            double zDiff = regionPoints.get(k).getZ() - regionPoints.get(i).getZ();

            area += xSum * zDiff;

            k = i;
        }

        return -(area / 2); // The convex hull traversal is negative
    }

    public void convexHull(List<RegionPoint> regionPoints, boolean updateMaxandMin) {
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

            if (updateMaxandMin) {
                max = min = points.get(0);
                regionPoints.forEach(this::processPoint);
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

    private void processPoint(RegionPoint newPoint) {
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
}