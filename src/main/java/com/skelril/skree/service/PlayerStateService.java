/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.entity.player.Player;

public interface PlayerStateService {
    @Deprecated // Temporary function
    void save(Player player, String saveName);
    @Deprecated // Temporary function
    void load(Player player, String saveName);
}
