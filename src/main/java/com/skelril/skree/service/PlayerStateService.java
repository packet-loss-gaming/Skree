/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.playerstate.InventoryStorageStateException;
import org.spongepowered.api.entity.living.player.Player;

public interface PlayerStateService {
    boolean hasInventoryStored(Player player);
    boolean hasReleasedInventoryStored(Player player);
    void storeInventory(Player player) throws InventoryStorageStateException;
    void loadInventory(Player player) throws InventoryStorageStateException;
    void releaseInventory(Player player) throws InventoryStorageStateException;

    @Deprecated // Temporary function
    void save(Player player, String saveName);
    @Deprecated // Temporary function
    void load(Player player, String saveName);
}
