/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.RespawnService;
import com.skelril.skree.service.internal.zone.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class LegacyZoneBase implements Zone {
    protected ZoneRegion region;
    private boolean expired = false;

    public LegacyZoneBase(ZoneRegion region) {
        this.region = region;
    }

    public boolean isActive() {
        return !expired;
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        RespawnService respawnService = Sponge.getServiceManager().provideUnchecked(RespawnService.class);

        Location<World> newLocation = respawnService.pop(player).orElse(respawnService.getDefault(player));
        player.setLocation(newLocation);

        return new Clause<>(player, ZoneStatus.REMOVED);
    }

    @Override
    public ZoneRegion getRegion() {
        return region;
    }

    @Override
    public Collection<Player> getPlayers(PlayerClassifier classifier) {
        return Sponge.getServer().getOnlinePlayers().stream().filter(this::contains).collect(Collectors.toList());
    }

    public Collection<Entity> getContained() {
        return getContained(Entity.class);
    }

    private Collection<Entity> __getContained(Predicate<Location<World>> locationPredicate, Predicate<Entity> entityPredicate) {
        return getRegion().getExtent().getEntities(e -> {
            return entityPredicate.test(e) && locationPredicate.test(e.getLocation());
        });
    }

    private class ZoneBoundingBoxPredicate implements Predicate<Location<World>> {

        private ZoneBoundingBox box;

        public ZoneBoundingBoxPredicate(ZoneBoundingBox box) {
            this.box = box;
        }

        @Override
        public boolean test(Location<World> location) {
            return contains(location) && box.contains(location.getPosition());
        }
    }

    private class EntityTypePredicate implements Predicate<Entity> {

        private List<EntityType> entityTypes;

        public EntityTypePredicate(List<EntityType> entityTypes) {
            this.entityTypes = entityTypes;
        }

        @Override
        public boolean test(Entity entity) {
            for (EntityType entityType : entityTypes) {
                if (entity.getType() == entityType) {
                    return true;
                }
            }
            return false;
        }
    }

    private class ClassPredicate implements Predicate<Entity> {

        private List<Class<?>> classes;

        public ClassPredicate(List<Class<?>> classes) {
            this.classes = classes;
        }

        @Override
        public boolean test(Entity entity) {
            for (Class<?> clazz : classes) {
                if (clazz.isInstance(entity)) {
                    return true;
                }
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getContained(Class<T> clazz) {
        return (Collection<T>) __getContained(this::contains, new ClassPredicate(Lists.newArrayList(clazz)));
    }

    public Collection<Entity> getContained(Class<?>... classes) {
        return __getContained(this::contains, new ClassPredicate(Lists.newArrayList(classes)));
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getContained(ZoneBoundingBox box, Class<T> clazz) {
        return (Collection<T>) __getContained(
                new ZoneBoundingBoxPredicate(box),
                new ClassPredicate(Lists.newArrayList(clazz))
        );
    }

    public Collection<Entity> getContained(ZoneBoundingBox box, Class<?>... classes) {
        return __getContained(
                new ZoneBoundingBoxPredicate(box),
                new ClassPredicate(Lists.newArrayList(classes))
        );
    }

    public Collection<Entity> getContained(EntityType... types) {
        return __getContained(this::contains, new EntityTypePredicate(Lists.newArrayList(types)));
    }

    public Collection<Entity> getContained(ZoneBoundingBox box, EntityType... types) {
        return __getContained(
                new ZoneBoundingBoxPredicate(box),
                new EntityTypePredicate(Lists.newArrayList(types))
        );
    }

    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    public boolean contains(Location<World> location) {
        return getRegion().getExtent().equals(location.getExtent()) && getRegion().contains(location.getPosition());
    }

    public void expire() {
        expired = true;
    }

    public boolean isEmpty() {
        return getPlayers(PlayerClassifier.PARTICIPANT).isEmpty();
    }

    public void remove() {
        remove(Monster.class, ExperienceOrb.class, Item.class, Arrow.class);
    }

    public void remove(Class<?>... classes) {
        getContained(classes).stream().filter(e -> !(e instanceof Player)).forEach(Entity::remove);
    }
}
