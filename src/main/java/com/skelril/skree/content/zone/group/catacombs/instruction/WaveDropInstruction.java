/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction;

import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.content.droptable.CofferResolver;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.item.CustomItemTypes.*;

public class WaveDropInstruction implements Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>> {
    private static final DropTable dropTable;

    static {
        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller();
        dropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(new CofferResolver(50), 12)
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                            newItemStack(ItemTypes.COOKED_BEEF)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.COOKED_CHICKEN)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.COOKED_FISH)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.COOKED_MUTTON)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.COOKED_PORKCHOP)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ItemTypes.COOKED_RABBIT)
                                                        )
                                                ), 50
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(COOKED_GOD_FISH)
                                                        )
                                                ), 500
                                        )
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(SCROLL_OF_SUMMATION)
                                                        )
                                                ), 350
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(ANCIENT_METAL_FRAGMENT)
                                                        )
                                                ), 700
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack(PHANTOM_CLOCK)
                                                        )
                                                ), 100000
                                        )
                                )
                        )
                )
        );
    }

    private double modifier;

    public WaveDropInstruction(double modifier) {
        this.modifier = modifier;
    }

    @Override
    public Optional<Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>>> apply(UnbindCondition condition, Boss<Zombie, CatacombsBossDetail> boss) {
        int wave = boss.getDetail().getWave();

        Collection<ItemStack> drops = dropTable.getDrops(
                wave,
                wave * .5 * modifier
        );

        Optional<Zombie> optEnt = boss.getTargetEntity();
        if (optEnt.isPresent()) {
            new ItemDropper(optEnt.get().getLocation()).dropStacks(drops, SpawnTypes.DROPPED_ITEM);
        }

        return Optional.empty();
    }
}