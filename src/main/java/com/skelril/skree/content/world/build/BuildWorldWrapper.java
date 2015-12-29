/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.build;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Living) || !isApplicable(entity)) {
            return;
        }

        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            Entity srcEntity;
            if (optDamageSource.isPresent() && optDamageSource.get() instanceof IndirectEntityDamageSource) {
                srcEntity = ((IndirectEntityDamageSource) optDamageSource.get()).getIndirectSource();
            } else {
                srcEntity = optDamageSource.get().getSource();
            }

            if (!(srcEntity instanceof Living)) {
                return;
            }

            Living living = (Living) srcEntity;
            if (entity instanceof Player && living instanceof Player) {
                processPvP((Player) living, (Player) entity, event);
            }
        }
    }

    private void processPvP(Player attacker, Player defender, DamageEntityEvent event) {
        Optional<PvPService> optService = SkreePlugin.inst().getGame().getServiceManager().provide(PvPService.class);
        if (optService.isPresent()) {
            PvPService service = optService.get();
            if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
                return;
            }
        }

        attacker.sendMessage(Texts.of(TextColors.RED, "PvP is opt-in only in build worlds!"));

        event.setCancelled(true);
    }
}