/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.main;


import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MainWorldWrapper extends WorldEffectWrapperImpl implements Runnable {

    public MainWorldWrapper() {
        this(new ArrayList<>());
    }

    public MainWorldWrapper(Collection<World> worlds) {
        super("Main", worlds);

        Task.builder().execute(this).interval(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
    }

    @Listener
    public void onEntityConstruction(ConstructEntityEvent.Pre event) {

        if (!isApplicable(event.getTransform().getExtent())) {
            return;
        }

        if (Monster.class.isAssignableFrom(event.getTargetType().getEntityClass())) {
            event.setCancelled(true);
        }
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

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        new PlayerCombatParser() {
            @Override
            public void processPvP(Player attacker, Player defender) {
                Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
                if (optService.isPresent()) {
                    PvPService service = optService.get();
                    if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
                        return;
                    }
                }

                attacker.sendMessage(Text.of(TextColors.RED, "PvP is opt-in only in the main world!"));

                event.setCancelled(true);
            }
        };
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
