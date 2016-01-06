/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.instance;

import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class InstanceWorldWrapper extends WorldEffectWrapperImpl {
    public InstanceWorldWrapper() {
        this(new ArrayList<>());
    }

    public InstanceWorldWrapper(Collection<World> worlds) {
        super("Instance", worlds);
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (isApplicable(player)) {
            Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
            if (optWorldService.isPresent()) {
                Collection<World> worlds = optWorldService.get().getEffectWrapper("Main").getWorlds();
                player.setLocation(worlds.iterator().next().getSpawnLocation());
            }
        }
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event) {
        if (isApplicable(event.getToTransform().getExtent())) {
            Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
            if (optWorldService.isPresent()) {
                Collection<World> worlds = optWorldService.get().getEffectWrapper("Main").getWorlds();
                event.setToTransform(new Transform<>(worlds.iterator().next().getSpawnLocation()));
            }
        }
    }
}