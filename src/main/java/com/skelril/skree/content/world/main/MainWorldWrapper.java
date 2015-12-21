/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.main;


import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MainWorldWrapper extends WorldEffectWrapperImpl implements Runnable {

    private SkreePlugin plugin;
    private Game game;

    public MainWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public MainWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Main", worlds);
        this.plugin = plugin;
        this.game = game;

        Task.builder().execute(this).interval(1, TimeUnit.SECONDS).submit(plugin);
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        List<Entity> entities = event.getEntities();

        for (Entity entity : entities) {
            if (!isApplicable(entity)) continue;

            if (entity instanceof Monster) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void run() {
        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities(p -> p.getType().equals(EntityTypes.PLAYER))) {
                Optional<PotionEffectData> optPotionData = entity.get(PotionEffectData.class);
                if (optPotionData.isPresent()) {
                    PotionEffect.Builder builder = PotionEffect.builder();
                    builder.potionType(PotionEffectTypes.SPEED);
                    builder.amplifier(5);
                    builder.duration(3 * 20);
                    builder.particles(false);

                    PotionEffectData potionData = optPotionData.get();
                    potionData.effects().add(builder.build());
                    entity.offer(potionData);
                }
            }
        }
    }
}
