/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.zone;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.world.World;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.zone.ZoneServiceImpl;
import com.skelril.skree.service.internal.zone.allocator.ChainPlacementAllocator;
import org.spongepowered.api.Game;

import java.io.File;

public class ZoneSystem {

    private ZoneService service;

    public ZoneSystem(SkreePlugin plugin, Game game) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        File baseDir = null;
        World world = null;
        service = new ZoneServiceImpl(new ChainPlacementAllocator(baseDir, world));
    }
}
