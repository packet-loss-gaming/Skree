/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.arrowfishing;


import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.block.MultiTypeRegistry;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.projectilewatcher.ProjectileTickEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.modifier.Modifiers.UBER_ARROW_FISHING;
import static com.skelril.skree.content.registry.item.CustomItemTypes.RAW_GOD_FISH;

public class ArrowFishingHandler {
    private DropTable dropTable;

    public ArrowFishingHandler() {
        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller((a, b) -> (int) (a + b));
        dropTable = new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                        new DropTableEntryImpl(
                                new SimpleDropResolver(
                                        Lists.newArrayList(
                                                newItemStack(ItemTypes.FISH)
                                        )
                                ),
                                100
                        ),
                        new DropTableEntryImpl(
                                new SimpleDropResolver(
                                        Lists.newArrayList(
                                                newItemStack((ItemType) RAW_GOD_FISH)
                                        )
                                ),
                                500
                        )
                )
        );
    }

    private boolean checkVelocity(Vector3d velocity) {
        return Math.abs(velocity.getX()) + Math.abs(velocity.getY()) > 2;
    }

    @Listener
    public void onProjectileTickEvent(ProjectileTickEvent event) {
        Projectile projectile = event.getTargetEntity();

        if (!(projectile instanceof Arrow) || Probability.getChance(3)) {
            return;
        }

        Location<World> loc = projectile.getLocation();

        if (MultiTypeRegistry.isWater(loc.getBlockType()) && checkVelocity(projectile.getVelocity())) {
            ProjectileSource source = projectile.getShooter();
            double modifier = 1;

            if (source instanceof Living) {
                modifier = 50;
            }

            Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
            int rolls = 1;
            if (optService.isPresent() && optService.get().isActive(UBER_ARROW_FISHING)) {
                if (source instanceof Living) {
                    rolls = 15;
                } else {
                    rolls = 5;
                }
            }

            new ItemDropper(loc).dropStacks(dropTable.getDrops(rolls, modifier), SpawnTypes.DROPPED_ITEM);
        }
    }
}
