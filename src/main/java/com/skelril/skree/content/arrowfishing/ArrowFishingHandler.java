/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.arrowfishing;


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
import com.skelril.skree.service.internal.projectilewatcher.TrackedProjectileInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
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
        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller((a, b) -> (int) (a * b));
        dropTable = new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                        new DropTableEntryImpl(
                                new SimpleDropResolver(
                                        Lists.newArrayList(
                                                newItemStack(ItemTypes.FISH)
                                        )
                                ),
                                32
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

    @Listener
    public void onProjectileTickEvent(ProjectileTickEvent event) {

        if (!(event.getTargetEntity() instanceof Arrow) || Probability.getChance(3)) {
            return;
        }

        Location<World> loc = event.getTargetEntity().getLocation();
        TrackedProjectileInfo info = event.getProjectileInfo();

        Optional<ProjectileSource> optSource = info.getCause().first(ProjectileSource.class);
        if (optSource.isPresent() && MultiTypeRegistry.isWater(loc.getBlockType())) {
            ProjectileSource source = optSource.get();
            double modifier = 1;

            if (source instanceof Living) {
                Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);

                if (optService.isPresent() && optService.get().isActive(UBER_ARROW_FISHING)) {
                    modifier *= 2;
                } else {
                    modifier *= 1.25;
                }
            }

            new ItemDropper(loc).dropItems(
                    dropTable.getDrops(1, modifier), Cause.source(event.getTargetEntity()).build()
            );
        }
    }
}
