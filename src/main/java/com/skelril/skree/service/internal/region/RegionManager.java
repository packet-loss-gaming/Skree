/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.skelril.skree.db.SQLHandle;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.skelril.skree.db.schema.Tables.*;

public class RegionManager {
    private final String worldName;

    private HashMap<UUID, RegionReference> regionMap = new HashMap<>();
    private List<RegionReference> regionList = new ArrayList<>();

    public RegionManager(String worldName) {
        this.worldName = worldName;
    }

    public void load() {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);

            int worldID = create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(worldName)).fetchOne().value1();

            List<Region> loaded = create.select(
                    REGIONS.ID,
                    REGIONS.UUID,
                    REGIONS.X,
                    REGIONS.Y,
                    REGIONS.Z,
                    REGIONS.NAME,
                    REGIONS.POWER
            ).from(REGIONS).where(REGIONS.WORLD_ID.equal(worldID)).fetch().stream().map(r -> {
                UUID regionID = UUID.fromString(r.getValue(REGIONS.UUID));

                RegionPoint masterBlock = new RegionPoint(
                        r.getValue(REGIONS.X),
                        r.getValue(REGIONS.Y),
                        r.getValue(REGIONS.Z)
                );

                String name = r.getValue(REGIONS.NAME);

                int power = r.getValue(REGIONS.POWER);

                Set<UUID> members = create.select(
                        PLAYERS.UUID
                ).from(REGION_MEMBERS).join(PLAYERS).on(REGION_MEMBERS.PLAYER_ID.equal(PLAYERS.ID)).where(
                        REGION_MEMBERS.REGION_ID.equal(r.getValue(REGIONS.ID))
                ).fetch().stream().map(a ->
                        UUID.fromString(a.value1())
                ).collect(Collectors.toSet());

                Set<RegionPoint> points = create.select(
                        REGION_POINTS.X,
                        REGION_POINTS.Y,
                        REGION_POINTS.Z
                ).from(REGION_POINTS).where(
                        REGION_POINTS.REGION_ID.equal(r.getValue(REGIONS.ID))
                ).fetch().stream().map(entry ->
                        new RegionPoint(entry.value1(), entry.value2(), entry.value3())
                ).collect(Collectors.toSet());

                return new Region(regionID, worldName, masterBlock, name, power, members, points);
            }).collect(Collectors.toList());

            uncheckedAddRegion(loaded);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void writeAddRegion(Collection<Region> newRegions) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            con.setAutoCommit(false);

            int worldID = create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(worldName)).fetchOne().value1();

            for (Region region : newRegions) {
                create.insertInto(REGIONS).columns(REGIONS.UUID, REGIONS.WORLD_ID, REGIONS.X, REGIONS.Y, REGIONS.Z, REGIONS.NAME, REGIONS.POWER)
                        .values(
                                region.getID().toString(),
                                worldID,
                                region.getMasterBlock().getX(),
                                region.getMasterBlock().getY(),
                                region.getMasterBlock().getZ(),
                                region.getName(),
                                region.getPowerLevel()
                        ).execute();

                region.writeInit(con);
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeRemRegion(Collection<Region> oldRegions) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            con.setAutoCommit(false);
            for (Region region : oldRegions) {
                create.deleteFrom(REGIONS).where(REGIONS.UUID.equal(region.getID().toString())).execute();
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
