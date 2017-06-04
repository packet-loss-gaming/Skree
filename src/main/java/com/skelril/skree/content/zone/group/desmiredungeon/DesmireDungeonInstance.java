/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.desmiredungeon;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class DesmireDungeonInstance extends LegacyZoneBase implements Runnable {
    private DesmireDungeonRoom[][] roomGrid = new DesmireDungeonRoom[5][5];
    private List<DesmireDungeonRoom> rooms = new ArrayList<>();
    private DesmireDungeonRoom activeRoom;

    public DesmireDungeonInstance(ZoneRegion region) {
        super(region);
    }

    private boolean isCenterRoom(int x, int z) {
        return x == (roomGrid.length / 2) && z == (roomGrid[x].length / 2);
    }

    private void configureRooms() {
        for (int x = 0; x < roomGrid.length; ++x) {
            for (int z = 0; z < roomGrid[x].length; ++z) {
                if (isCenterRoom(x, z)) {
                    continue;
                }

                Vector3i roomSize = new Vector3i(18, 8, 18);
                Vector3i roomOrigin = getRegion().getMinimumPoint().add(x * 17, 0, z * 17);
                ZoneBoundingBox roomBoundingBox = new ZoneBoundingBox(roomOrigin, roomSize);

                DesmireDungeonRoom room = new DesmireDungeonRoom(this, roomBoundingBox);

                room.lockDoors();

                rooms.add(roomGrid[x][z] = room);
            }
        }
    }

    private DesmireDungeonRoom getRandomRoom() {
        return Probability.pickOneOf(rooms);
    }

    private void setStartingRoom() {
        activeRoom = getRandomRoom();
        activeRoom.unlockDoors();
    }

    private void setUp() {
        configureRooms();
        setStartingRoom();
    }

    @Override
    public boolean init() {
        setUp();
        remove();
        return true;
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
        remove();
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(activeRoom.getSpawnPoint());
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    public Optional<DesmireDungeonRoom> getCurrentlyOccupiedRoom() {
        Set<DesmireDungeonRoom> usedRooms = new HashSet<>();

        for (Player player : getPlayers(PARTICIPANT)) {
            for (DesmireDungeonRoom room : rooms) {
                if (room.contains(player)) {
                    usedRooms.add(room);
                }
            }
        }

        if (usedRooms.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(usedRooms.iterator().next());
    }

    public void checkDungeonProgression() {
        Optional<DesmireDungeonRoom> optRoom = getCurrentlyOccupiedRoom();
        if (!optRoom.isPresent()) {
            return;
        }

        DesmireDungeonRoom room = optRoom.get();
        if (room != activeRoom) {
            activeRoom.lockDoors();
            activeRoom = room;
            activeRoom.summonCreatures();
            return;
        }

        int containedCount = getContained(room.getBoundingBox(), Monster.class).size();
        if (containedCount < 1) {
            activeRoom.unlockDoors();
        }
    }

    @Override
    public void run() {
        if (isEmpty()) {
            expire();
            return;
        }

        checkDungeonProgression();
    }
}
