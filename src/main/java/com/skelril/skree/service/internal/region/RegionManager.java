/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.skelril.skree.content.registry.block.CustomBlockTypes;
import com.skelril.skree.db.SQLHandle;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.skelril.skree.db.schema.Tables.*;

public class RegionManager {
    private final String worldName;

    private HashMap<UUID, CachedRegion> regionMap = new HashMap<>();
    private List<CachedRegion> regionList = new ArrayList<>();

    public RegionManager(String worldName) {
        this.worldName = worldName;
    }

    public void load() {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);

            int worldID = create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(worldName)).fetchOne().value1();

            List<RegionDatabaseHandle> loaded = create.select(
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

                return new RegionDatabaseHandle(regionID, worldName, masterBlock, name, power, members, points);
            }).collect(Collectors.toList());

            uncheckedAddRegion(loaded);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CachedRegion addRegion(RegionDatabaseHandle region) {
        return addRegion(Collections.singleton(region)).get(0);
    }

    public List<CachedRegion> addRegion(Collection<RegionDatabaseHandle> regions) {
        writeAddRegion(regions);
        return uncheckedAddRegion(regions);
    }

    public void remRegion(RegionDatabaseHandle region) {
        remRegion(Collections.singleton(region));
    }

    public void remRegion(Collection<RegionDatabaseHandle> regions) {
        writeRemRegion(regions);
        uncheckedRemRegion(regions);
    }

    private void writeAddRegion(Collection<RegionDatabaseHandle> newRegions) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            con.setAutoCommit(false);

            int worldID = create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(worldName)).fetchOne().value1();

            for (RegionDatabaseHandle region : newRegions) {
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

    private void writeRemRegion(Collection<RegionDatabaseHandle> oldRegions) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            con.setAutoCommit(false);
            for (RegionDatabaseHandle region : oldRegions) {
                create.deleteFrom(REGIONS).where(REGIONS.UUID.equal(region.getID().toString())).execute();
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<CachedRegion> uncheckedAddRegion(Collection<RegionDatabaseHandle> regions) {
        List<CachedRegion> regionReferences = new ArrayList<>();
        regions.stream().filter(region -> !regionMap.containsKey(region.getID())).forEach(region -> {
            CachedRegion ref = new CachedRegion(region, this);
            regionMap.put(region.getID(), ref);
            regionList.add(ref);
            regionReferences.add(ref);
        });
        return regionReferences;
    }

    private void uncheckedRemRegion(Collection<RegionDatabaseHandle> regions) {
        for (RegionDatabaseHandle region : regions) {
            regionList.remove(regionMap.remove(region.getID()));
        }
    }

    public Optional<CachedRegion> getRegion(RegionPoint point) {
        for (CachedRegion region : regionList) {
            if (region.contains(point)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    public Optional<CachedRegion> getMarkedRegion(RegionPoint point) {
        for (CachedRegion region : regionList) {
            if (region.isMarkedPoint(point)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    public boolean createsIntersect(CachedRegion addedTo, RegionPoint point) {
        for (CachedRegion region : regionList) {
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

    public int cleanup() {
        Optional<World> optWorld = Sponge.getServer().getWorld(worldName);

        int total = 0;

        if (optWorld.isPresent()) {
            World world = optWorld.get();
            for (CachedRegion region : regionList) {
                List<RegionPoint> toRemove = new ArrayList<>();
                for (RegionPoint point : region.getFullPoints()) {
                    BlockType type = world.getBlockType(point.toInt());
                    if (type != CustomBlockTypes.REGION_MASTER && type != CustomBlockTypes.REGION_MARKER) {
                        ++total;
                        toRemove.add(point);
                    }
                }
                region.remPoint(toRemove);
            }
        }

        return total;
    }

    public boolean contains(RegionPoint point) {
        return getRegion(point).isPresent();
    }

    public double getPowerToAreaConstant() {
        return 500;
    }
}
