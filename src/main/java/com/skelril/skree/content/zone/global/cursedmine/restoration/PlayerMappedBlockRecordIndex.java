/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.restoration;

import com.flowpowered.math.vector.Vector3i;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMappedBlockRecordIndex implements BlockRecordIndex {

    private Map<UUID, Map<Vector3i, BlockRecord>> recordMap = new ConcurrentHashMap<>();

    public boolean addItem(UUID player, BlockRecord record) {

        if (!recordMap.containsKey(player)) {
            recordMap.put(player, new HashMap<>());
        }
        Map<Vector3i, BlockRecord> playerRecord = recordMap.get(player);
        Vector3i blockPos = record.getLocation().getBlockPosition();
        if (playerRecord.containsKey(blockPos)) {
            return false;
        }
        playerRecord.put(blockPos, record);
        return true;
    }

    @Override
    public void revertByTime(long time) {

        Iterator<Map<Vector3i, BlockRecord>> primeIt = recordMap.values().iterator();
        Map<Vector3i, BlockRecord> activeRecordList;
        while (primeIt.hasNext()) {
            activeRecordList = primeIt.next();

            Iterator<Map.Entry<Vector3i, BlockRecord>> it = activeRecordList.entrySet().iterator();
            BlockRecord activeRecord;
            while (it.hasNext()) {
                activeRecord = it.next().getValue();
                if (System.currentTimeMillis() - activeRecord.getTime() >= time) {
                    activeRecord.revert();
                    it.remove();
                }
            }

            if (activeRecordList.isEmpty()) {
                primeIt.remove();
            }
        }
    }

    public boolean hasRecordForPlayer(UUID player) {

        return recordMap.containsKey(player);
    }

    public void revertByPlayer(UUID player) {

        if (!hasRecordForPlayer(player)) return;

        Map<Vector3i, BlockRecord> activeRecordList = recordMap.get(player);

        if (activeRecordList.isEmpty()) {
            recordMap.remove(player);
            return;
        }

        Iterator<Map.Entry<Vector3i, BlockRecord>> it = activeRecordList.entrySet().iterator();
        BlockRecord activeRecord;
        while (it.hasNext()) {
            activeRecord = it.next().getValue();
            activeRecord.revert();
            it.remove();
        }

        recordMap.remove(player);
    }

    @Override
    public void revertAll() {

        Iterator<Map<Vector3i, BlockRecord>> primeIt = recordMap.values().iterator();
        Map<Vector3i, BlockRecord> activeRecordList;
        while (primeIt.hasNext()) {
            activeRecordList = primeIt.next();

            Iterator<Map.Entry<Vector3i, BlockRecord>> it = activeRecordList.entrySet().iterator();
            BlockRecord activeRecord;
            while (it.hasNext()) {
                activeRecord = it.next().getValue();
                activeRecord.revert();
                it.remove();
            }

            if (activeRecordList.isEmpty()) {
                primeIt.remove();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerMappedBlockRecordIndex && recordMap.equals(((PlayerMappedBlockRecordIndex) o).recordMap);
    }

    @Override
    public int size() {

        return recordMap.size();
    }
}