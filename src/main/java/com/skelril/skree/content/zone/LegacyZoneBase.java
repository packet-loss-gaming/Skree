/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
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

    private Collection<Entity> __getContained(Class<?>... classes) {
        return getRegion().getExtent().getEntities(e -> {
            for (Class<?> clazz : classes) {
                if (clazz.isInstance(e)) {
                    return contains(e.getLocation());
                }
            }
            return false;
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getContained(Class<T> clazz) {
        return (Collection<T>) __getContained(clazz);
    }

    public Collection<Entity> getContained(Class<?>... classes) {
        return __getContained(classes);
    }

    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    public boolean contains(Location<World> location) {
        if (getRegion().getExtent().equals(location.getExtent())) {
            Vector3i min = getRegion().getMinimumPoint();
            Vector3i max = getRegion().getMaximumPoint();

            boolean withinX = location.getX() > min.getX() && location.getX() < max.getX();
            boolean withinY = location.getY() > min.getY() && location.getY() < max.getY();
            boolean withinZ = location.getZ() > min.getZ() && location.getZ() < max.getZ();

            return withinX && withinY && withinZ;
        }
        return false;
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
