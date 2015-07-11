/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.build;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;

import static com.skelril.skree.content.registry.TypeCollections.ore;

public class BuildWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public BuildWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public BuildWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Build", worlds);
        this.plugin = plugin;
        this.game = game;
    }

    @Subscribe
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!isApplicable(event.getLocation().getExtent())) return;

        // TODO Smarter "should this mob be allowed to spawn" code
        if (event.getEntity() instanceof Monster) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onBlockBreak(BlockBreakEvent event) {
        Location block = event.getBlock();
        if (!isApplicable(block.getExtent())) return;

        if (ore().contains(block.getBlockType())) {
            event.setCancelled(true);
            block.setBlockType(BlockTypes.STONE);
        }
    }
}