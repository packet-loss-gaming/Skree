/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class DeadlyPotionCurse implements Consumer<Player> {
    private static Random random = new Random(System.currentTimeMillis());

    private void throwSlashPotion(Location<World> location) {

        PotionEffectType[] thrownTypes = new PotionEffectType[]{
                PotionEffectTypes.INSTANT_DAMAGE, PotionEffectTypes.INSTANT_DAMAGE,
                PotionEffectTypes.POISON, PotionEffectTypes.WEAKNESS
        };

        Optional<Entity> optEntity = location.getExtent().createEntity(EntityTypes.SPLASH_POTION, location.getPosition());
        if (optEntity.isPresent()) {
            PotionEffectType type = Probability.pickOneOf(thrownTypes);
            PotionEffect effect = PotionEffect.of(type, 2, type.isInstant() ? 1 : 15 * 20);

            Entity entity = optEntity.get();
            entity.setVelocity(new Vector3d(
                    random.nextDouble() * .5 - .25,
                    random.nextDouble() * .4 + .1,
                    random.nextDouble() * .5 - .25
            ));
            entity.offer(Keys.POTION_EFFECTS, Lists.newArrayList(effect));

            location.getExtent().spawnEntity(entity, Cause.source(this).build());
        }
    }

    @Override
    public void accept(Player player) {
        throwSlashPotion(player.getLocation());
    }
}
