/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import java.util.*;

public class RegionManager {
    private HashMap<UUID, RegionReference> regionMap = new HashMap<>();
    private List<RegionReference> regionList = new ArrayList<>();

    public void load() {

    }

    public RegionReference addRegion(Region region) {
        return addRegion(Collections.singleton(region)).get(0);
    }

    public List<RegionReference> addRegion(Collection<Region> regions) {
        writeAddRegion(regions);
        return uncheckedAddRegion(regions);
    }

    public void remRegion(Region region) {
        remRegion(Collections.singleton(region));
    }

    public void remRegion(Collection<Region> regions) {
        writeRemRegion(regions);
        uncheckedRemRegion(regions);
    }

    private void writeAddRegion(Collection<Region> region) {

    }

    private void writeRemRegion(Collection<Region> region) {

    }

    private List<RegionReference> uncheckedAddRegion(Collection<Region> regions) {
        List<RegionReference> regionReferences = new ArrayList<>();
        for (Region region : regions) {
            if (!regionMap.containsKey(region.getID())) {
                RegionReference ref = new RegionReference(region, this);
                regionMap.put(region.getID(), ref);
                regionList.add(ref);
                regionReferences.add(ref);
            }
        }
        return regionReferences;
    }

    private void uncheckedRemRegion(Collection<Region> regions) {
        for (Region region : regions) {
            regionList.remove(regionMap.remove(region.getID()));
        }
    }

    public Optional<RegionReference> getRegion(RegionPoint point) {
        for (RegionReference region : regionList) {
            if (region.contains(point)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    public Optional<RegionReference> getMarkedRegion(RegionPoint point) {
        for (RegionReference region : regionList) {
            if (region.isMarkedPoint(point)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    public boolean createsIntersect(RegionReference addedTo, RegionPoint point) {
        for (RegionReference region : regionList) {
            if (addedTo.equals(region)) {
                continue;
            }

            if (region.uncheckedContains(point)) {
//            if (region.possiblyContains(point)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(RegionPoint point) {
        return getRegion(point).isPresent();
    }
}
