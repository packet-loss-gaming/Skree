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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
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

    private boolean check(Player player, Location<World> loc) {
        return isApplicable(loc) && !player.hasPermission("skree.admin.edit.main");
    }

    @Listener
    public void onBlockChange(ChangeBlockEvent event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            for (Transaction<BlockSnapshot> block : event.getTransactions()) {
                Optional<Location<World>> optLoc = block.getOriginal().getLocation();
                if (optLoc.isPresent()) {
                    if (check(player, optLoc.get())) {
                        event.setCancelled(true);
                        if (event.getCause().root().equals(player)) {
                            player.sendMessage(Text.of(TextColors.RED, "You can't change blocks here!"));
                        }
                        return;
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        if (!isApplicable(event.getTargetEntity())) {
            return;
        }

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

            @Override
            public void processNonLivingAttack(DamageSource attacker, Player defender) {
                if (attacker.getType() == DamageTypes.VOID) {
                    defender.setLocation(defender.getWorld().getSpawnLocation());
                    event.setCancelled(true);
                }
            }
        }.parse(event);
    }

    @Override
    public void run() {
        PotionEffect speedEffect = PotionEffect.builder()
                .duration(3 * 20)
                .amplifier(5)
                .particles(false)
                .potionType(PotionEffectTypes.SPEED)
                .build();

        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities(p -> p.getType().equals(EntityTypes.PLAYER))) {
                if (entity.get(Keys.GAME_MODE).orElse(GameModes.CREATIVE) != GameModes.SURVIVAL) {
                    continue;
                }

                List<PotionEffect> potionEffects = entity.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
                potionEffects.add(speedEffect);
                entity.offer(Keys.POTION_EFFECTS, potionEffects);
            }
        }
    }
}
