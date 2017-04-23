/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.templeoffate;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class TempleOfFateInstance extends LegacyZoneBase {

    private Location<World> startingPoint;
    private DropTable dropTable;

    public TempleOfFateInstance(ZoneRegion region) {
        super(region);
    }

    private void setUp() {
        startingPoint = new Location<>(getRegion().getExtent(), getRegion().getMinimumPoint().add(4, 11, 58));

        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller();
        dropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                    Lists.newArrayList(
                                                            newItemStack(Sponge.getRegistry().getType(ItemType.class, "skree:ancient_metal_fragment").get())
                                                    )
                                                ), 1
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(Sponge.getRegistry().getType(ItemType.class, "skree:emblem_of_hallow").get())
                                                        )
                                                ), 30
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(Sponge.getRegistry().getType(ItemType.class, "skree:emblem_of_the_forge").get())
                                                        )
                                                ), 30
                                        )
                                )
                        )
                )
        );
    }

    public void rewardPlayer(Player player) {
        for (ItemStack stack : dropTable.getDrops(1)) {
            player.getInventory().offer(stack);
        }
        remove(player);
    }

    @Override
    public boolean init() {
        remove();
        setUp();
        return true;
    }

    @Override
    public void forceEnd() {
        remove(getPlayers(PARTICIPANT));
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(startingPoint);
        return new Clause<>(player, ZoneStatus.ADDED);
    }
}
