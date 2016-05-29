/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.respawn;

import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.service.RespawnService;
import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class RespawnServiceImpl implements RespawnService {
    private Map<Player, Stack<Location<World>>> playerRespawnStack = new WeakHashMap<>();

    @Override
    public Location<World> getDefault(Player target) {
        WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

        Optional<Map<UUID, RespawnLocation>> optRespawnLocations = target.get(Keys.RESPAWN_LOCATIONS);
        if (optRespawnLocations.isPresent()) {
            BuildWorldWrapper buildWrapper = service.getEffectWrapper(BuildWorldWrapper.class).get();
            UUID buildWorldId = buildWrapper.getPrimaryWorld().getUniqueId();

            RespawnLocation targetLocation = optRespawnLocations.get().get(buildWorldId);
            if (targetLocation != null) {
                Optional<Location<World>> optLocation = targetLocation.asLocation();
                if (optLocation.isPresent()) {
                    return optLocation.get();
                }
            }
        }

        return service.getEffectWrapper(MainWorldWrapper.class).get().getPrimaryWorld().getSpawnLocation();
    }

    @Override
    public void push(Player player, Location<World> target) {
        playerRespawnStack.putIfAbsent(player, new Stack<>());
        playerRespawnStack.get(player).push(target);
    }

    @Override
    public Optional<Location<World>> peek(Player player) {
        Stack<Location<World>> stack = playerRespawnStack.getOrDefault(player, new Stack<>());
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack.peek());
    }

    @Override
    public Optional<Location<World>> pop(Player player) {
        Stack<Location<World>> stack = playerRespawnStack.getOrDefault(player, new Stack<>());
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack.pop());
    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
        event.setToTransform(new Transform<>(pop(player).orElse(getDefault(player))));
    }
}
