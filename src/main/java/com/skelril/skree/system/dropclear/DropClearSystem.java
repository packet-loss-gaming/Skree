/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.dropclear;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.DropClearService;
import com.skelril.skree.service.internal.dropclear.DropClearServiceImpl;
import org.spongepowered.api.Game;

public class DropClearSystem {

    private DropClearService service;

    public DropClearSystem(SkreePlugin plugin, Game game) {
        service = new DropClearServiceImpl(plugin, game);

        game.getSyncScheduler().runRepeatingTask(
                plugin,
                () -> game.getServer().getWorlds().stream().forEach(service::checkedCleanup),
                10
        );
    }
}
