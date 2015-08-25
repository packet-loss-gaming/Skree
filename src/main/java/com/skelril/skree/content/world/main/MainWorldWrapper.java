/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.main;

import com.google.common.base.Optional;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import net.minecraft.entity.passive.EntityChicken;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectTypes;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
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

        game.getScheduler().createTaskBuilder().execute(this).interval(1, TimeUnit.SECONDS).submit(plugin);
    }

    @Subscribe
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!isApplicable(event.getLocation().getExtent())) return;

        Entity entity = event.getEntity();

        // TODO Smarter "should this mob be allowed to spawn" code
        if (entity instanceof Monster) {
            event.setCancelled(true);
        }

        if (entity instanceof EntityChicken && ((EntityChicken) entity).func_152116_bZ()) {
            event.setCancelled(true);
        }
    }

    @Override
    public void run() {
        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities(p -> p.getType() == EntityTypes.PLAYER)) {
                Optional<PotionEffectData> optPotionData = entity.get(PotionEffectData.class);
                if (optPotionData.isPresent()) {
                    PotionEffectBuilder builder = game.getRegistry().createPotionEffectBuilder();
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
