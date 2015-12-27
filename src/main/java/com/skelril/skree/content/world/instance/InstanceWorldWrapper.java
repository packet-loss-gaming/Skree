/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.instance;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import com.skelril.skree.service.internal.zone.Zone;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class InstanceWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public InstanceWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public InstanceWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Instance", worlds);
        this.plugin = plugin;
        this.game = game;
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (isApplicable(player)) {
            Optional<WorldService> optWorldService = SkreePlugin.inst().getGame().getServiceManager().provide(WorldService.class);
            if (optWorldService.isPresent()) {
                Collection<World> worlds = optWorldService.get().getEffectWrapper("Main").getWorlds();
                player.setLocation(worlds.iterator().next().getSpawnLocation());
            }
        }
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {

        // Item Expires shouldn't be filtered anyways, but this is added
        // due to getTargetWorld being unimplemented for the ItemExpireEvent
        // at the time of writing
        if (event instanceof ItemExpireEvent || !isApplicable(event.getTargetWorld())) {
            return;
        }

        if (event.getCause().containsType(Zone.class)) {
            return;
        }

        // Remove every entity that is an agent not spawned by a Zone
        event.getEntities().removeAll(event.filterEntities(e -> !(e instanceof Agent)));
    }
}