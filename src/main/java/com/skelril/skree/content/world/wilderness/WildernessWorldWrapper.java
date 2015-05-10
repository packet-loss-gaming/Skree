/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.generator.FixedIntGenerator;
import com.skelril.skree.item.ItemFountain;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import com.skelril.skree.time.TimedRunnable;
import com.skelril.skree.util.Probability;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class WildernessWorldWrapper extends WorldEffectWrapperImpl {

    private SkreePlugin plugin;
    private Game game;

    public WildernessWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public WildernessWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Wilderness", worlds);
        this.plugin = plugin;
        this.game = game;
    }

    private static Set<BlockType> orePoolTypes = new HashSet<>();

    static {
        orePoolTypes.add(BlockTypes.COAL_ORE);
        orePoolTypes.add(BlockTypes.DIAMOND_ORE);
        orePoolTypes.add(BlockTypes.EMERALD_ORE);
        orePoolTypes.add(BlockTypes.REDSTONE_ORE);
        orePoolTypes.add(BlockTypes.GOLD_ORE);
        orePoolTypes.add(BlockTypes.IRON_ORE);
        orePoolTypes.add(BlockTypes.LAPIS_ORE);
        orePoolTypes.add(BlockTypes.LIT_REDSTONE_ORE);
        orePoolTypes.add(BlockTypes.QUARTZ_ORE);
    }

    @Subscribe
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isApplicable(event.getBlock().getExtent())) return;

        BlockType type = event.getBlock().getType();
        if (orePoolTypes.contains(type)) {
            // TODO add fortune & silk touch support
            addPool(event.getBlock(), 0, false);
        }
    }

    public int getLevel(Location location) {

        // Not in Wilderness
        if (!isApplicable(location.getExtent())) {
            return 0;
        }

        // In Wilderness
        return Math.max(0, Math.max(Math.abs(location.getBlockX()), Math.abs(location.getBlockZ())) / 1000) + 1;
    }

    public int getOreMod(int level) {
        double modifier = Math.max(1, (level * 3));
//        if (getModifierManager().isActive(ModifierType.DOUBLE_WILD_ORES)) {
//            modifier *= 2;
//        }
        return (int) modifier;
    }

    private void addPool(Location block, int fortune, boolean hasSilkTouch) {

        // ItemStack generalDrop = EnvironmentUtil.getOreDrop(block, hasSilkTouch);
        // fortune = EnvironmentUtil.isOre(generalDrop.getTypeId()) ? 0 : fortuneLevel;
        final int times = Probability.getRandom(getOreMod(getLevel(block)));
        ItemStack stack = game.getRegistry().getItemBuilder().itemType(ItemTypes.DIRT).build();
        ItemFountain fountain = new ItemFountain(
                game,
                toWorld.from(block.getExtent()),
                block.getPosition(),
                new FixedIntGenerator(fortune),
                Collections.singletonList(stack)
        ) {
            @Override
            public boolean run(int timesL) {
                getWorld().playSound(
                        SoundTypes.BLAZE_BREATH,
                        getPos(),
                        Math.min(
                                1,
                                (((float) timesL / times) * .6F) + ((float) 1 / times)
                        ),
                        0
                );
                return super.run(timesL);
            }

            @Override
            public void end() {
                getWorld().playSound(SoundTypes.BLAZE_BREATH, getPos(), .2F, 0);
            }
        };

        TimedRunnable runnable = new TimedRunnable(fountain, times);
        Task task = game.getSyncScheduler().runRepeatingTaskAfter(plugin, runnable, 20, 20).get();
        runnable.setTask(task);
    }
}
