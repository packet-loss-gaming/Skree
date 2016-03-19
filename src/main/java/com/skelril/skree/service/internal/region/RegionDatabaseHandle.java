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

import static com.skelril.skree.db.schema.Tables.*;

class RegionDatabaseHandle {
    private final UUID regionID;
    private final String worldName;
    private final RegionPoint masterBlock;
    private String name;
    private int powerLevel;
    private Set<UUID> members = new HashSet<>();
    private Set<RegionPoint> fullPoints;

    protected RegionDatabaseHandle(UUID regionID, String worldName, RegionPoint masterBlock, Set<UUID> members) {
        this(regionID, worldName, masterBlock, "Fluffy Bunnies", 200, members, new HashSet<>());
    }

    protected RegionDatabaseHandle(UUID regionID, String worldName, RegionPoint masterBlock, String name, int powerLevel, Set<UUID> members, Set<RegionPoint> fullPoints) {
        this.regionID = regionID;
        this.worldName = worldName;
        this.masterBlock = masterBlock;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.matches("[A-Za-z ']+")) {
            throw new IllegalArgumentException("Name did not match valid pattern.");
        }
        writeNameChangeToDB(this.name = name);
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
        members.removeAll(oldMembers);
    }

    protected void writeInit(Connection con) throws SQLException {
        writeMemberAdditionToDBWithCon(getMembers(), con);
        writePointAdditionToDBWithCon(getFullPoints(), con);
    }

    private void writeNameChangeToDB(String newName) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            create.update(REGIONS).set(
                    REGIONS.NAME, newName
            ).where(REGIONS.UUID.equal(regionID.toString())).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeMemberAdditionToDB(Collection<UUID> newMembers) {
        try (Connection con = SQLHandle.getConnection()) {
            writeMemberAdditionToDBWithCon(newMembers, con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeMemberAdditionToDBWithCon(Collection<UUID> newMembers, Connection con) throws SQLException {
        DSLContext create = DSL.using(con);
        int rgID = create.select(REGIONS.ID).from(REGIONS).where(
                REGIONS.UUID.equal(regionID.toString())
        ).fetchOne().value1();

        con.setAutoCommit(false);
        for (UUID member : newMembers) {
            create.insertInto(REGION_MEMBERS).columns(
                    REGION_MEMBERS.PLAYER_ID, REGION_MEMBERS.REGION_ID
            ).select(
                    create.select(
                            PLAYERS.ID,
                            DSL.value(rgID)
                    ).from(PLAYERS).where(PLAYERS.UUID.equal(member.toString()))
            ).execute();
        }
        con.commit();
    }

    private void writeMemberRemovalFromDB(Collection<UUID> oldMembers) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            int rgID = create.select(REGIONS.ID).from(REGIONS).where(
                    REGIONS.UUID.equal(regionID.toString())
            ).fetchOne().value1();

            con.setAutoCommit(false);
            for (UUID member : oldMembers) {
                create.deleteFrom(REGION_MEMBERS).where(
                        REGION_MEMBERS.REGION_ID.equal(rgID).and(
                                REGION_MEMBERS.PLAYER_ID.equal(DSL.select(
                                        PLAYERS.ID
                                ).from(PLAYERS).where(
                                        PLAYERS.UUID.equal(member.toString()))
                                )
                        )
                ).execute();
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void writePointAdditionToDB(Collection<RegionPoint> newPoints) {
        try (Connection con = SQLHandle.getConnection()) {
            writePointAdditionToDBWithCon(newPoints, con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writePointAdditionToDBWithCon(Collection<RegionPoint> newPoints, Connection con) throws SQLException {
        DSLContext create = DSL.using(con);
        con.setAutoCommit(false);
        for (RegionPoint point : newPoints) {
            create.insertInto(REGION_POINTS).columns(
                    REGION_POINTS.REGION_ID, REGION_POINTS.X, REGION_POINTS.Y, REGION_POINTS.Z
            ).select(
                    create.select(REGIONS.ID,
                            DSL.value(point.getX()), DSL.value(point.getY()), DSL.value(point.getZ())
                    ).from(REGIONS).where(REGIONS.UUID.equal(regionID.toString()))
            ).execute();
        }
        con.commit();
    }

    private void writePointRemovalFromDB(Collection<RegionPoint> oldPoints) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            con.setAutoCommit(false);
            for (RegionPoint point : oldPoints) {
                create.deleteFrom(REGION_POINTS).where(
                        REGION_POINTS.REGION_ID.equal(
                                DSL.select(REGIONS.ID).from(REGIONS).where(REGIONS.UUID.equal(regionID.toString()))
                        ).and(REGION_POINTS.X.equal(DSL.value(point.getX())))
                                .and(REGION_POINTS.Y.equal(DSL.value(point.getY())))
                                .and(REGION_POINTS.Z.equal(DSL.value(point.getZ()))))
                        .execute();
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
