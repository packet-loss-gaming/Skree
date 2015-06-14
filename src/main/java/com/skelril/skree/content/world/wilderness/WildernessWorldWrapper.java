/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.google.common.base.Optional;
import com.skelril.nitro.generator.FixedIntGenerator;
import com.skelril.nitro.item.ItemFountain;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.block.DropRegistry;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spongepowered.api.Game;
import org.spongepowered.api.attribute.Attributes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.AttributeData;
import org.spongepowered.api.data.manipulator.entity.ExplosiveRadiusData;
import org.spongepowered.api.data.manipulator.entity.HealthData;
import org.spongepowered.api.data.manipulator.item.EnchantmentData;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.BlockPlaceEvent;
import org.spongepowered.api.event.entity.EntityBreakBlockEvent;
import org.spongepowered.api.event.entity.EntityExplosionEvent;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.event.entity.living.LivingDeathEvent;
import org.spongepowered.api.event.entity.player.PlayerPlaceBlockEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    @Subscribe
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!isApplicable(event.getLocation().getExtent())) return;

        Entity entity = event.getEntity();
        Location loc = event.getLocation();

        final int level = getLevel(loc);

        if (entity instanceof Monster && level > 1) {
            Optional<HealthData> healthData = entity.getData(HealthData.class);
            if (healthData.isPresent()) {
                HealthData health = healthData.get();
                final double max = health.getMaxHealth();

                double newMax = max * level;

                health.setMaxHealth(newMax);
                health.setHealth(newMax);

                entity.offer(health);
            }

            Optional<AttributeData> attributeData = entity.getData(AttributeData.class);
            if (attributeData.isPresent()) {
                AttributeData attributes = attributeData.get();
                attributes.setBase(Attributes.GENERIC_ATTACK_DAMAGE, 2 + (level * 2));

                entity.offer(attributes);
            }
        }

        Optional<ExplosiveRadiusData> explosiveData = event.getEntity().getData(ExplosiveRadiusData.class);

        if (explosiveData.isPresent()) {
            ExplosiveRadiusData explosive = explosiveData.get();
            float min = explosive.getExplosionRadius();
            explosive.setExplosionRadius(
                    (int) Math.min(
                            entity instanceof Fireball ? 4 : 9,
                            Math.max(min, (min + level) / 2)
                    )
            );
            entity.offer(explosive);
        }
    }

    @Subscribe
    public void onEntityDeath(LivingDeathEvent event) {
        if (!isApplicable(event.getLocation().getExtent())) return;

        Entity entity = event.getEntity();
        Location loc = event.getLocation();

        if (entity instanceof Monster) {
            event.setExp(event.getExp() * getLevel(loc));
        }
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
    public void onBlockBreak(EntityBreakBlockEvent event) {
        Location loc = event.getBlock();
        if (!isApplicable(loc.getExtent())) return;

        BlockType type = loc.getType();
        if (orePoolTypes.contains(type)) {
            orePool:
            {
                Entity entity = event.getEntity();

                int fortuneMod = 0;
                boolean silkTouch = false;

                if (entity instanceof ArmorEquipable) {
                    Optional<ItemStack> held = ((ArmorEquipable) entity).getItemInHand();
                    if (held.isPresent()) {
                        ItemStack stack = held.get();

                        // TODO Currently abusing NMS to determine "breakability"
                        ItemType itemType = stack.getItem();
                        BlockType blockType = loc.getType();
                        if (itemType instanceof Item && blockType instanceof Block) {
                            if (!((Item) stack.getItem()).canHarvestBlock((Block) blockType)) {
                                break orePool;
                            }
                        }

                        Optional<EnchantmentData> optEnchantData = stack.getData(EnchantmentData.class);
                        if (optEnchantData.isPresent()) {
                            EnchantmentData enchantmentData = optEnchantData.get();

                            // Handle fortune
                            Optional<Integer> optFortune = enchantmentData.get(Enchantments.FORTUNE);
                            if (optFortune.isPresent()) {
                                fortuneMod = optFortune.get();
                            }

                            // Handle silk touch
                            Optional<Integer> optSilkTouch = enchantmentData.get(Enchantments.SILK_TOUCH);
                            if (optSilkTouch.isPresent()) {
                                silkTouch = true;
                            }
                        }
                    } else if (entity instanceof Player) {
                        break orePool;
                    }
                }

                addPool(loc, fortuneMod, silkTouch);
            }
        }
        // TODO needs updated XP API
        // event.setExp(event.getExp() * getLevel(loc));
    }

    @Subscribe
    public void onExplode(EntityExplosionEvent event) {
        Location origin = event.getExplosionLocation();
        if (!isApplicable(origin.getExtent())) return;

        event.setYield(Probability.getRangedRandom(event.getYield(), 100));

        for (Location loc : event.getBlocks()) {
            BlockType type = loc.getType();
            if (orePoolTypes.contains(type)) {
                addPool(loc, 0, false);
            }
        }
    }

    @Subscribe
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock();
        if (!isApplicable(loc.getExtent())) return;
        if (orePoolTypes.contains(loc.getType())) {
            if (event instanceof PlayerPlaceBlockEvent) {
                Player player = ((PlayerPlaceBlockEvent) event).getEntity();

                // Allow creative mode players to still place blocks
                if (player.getGameModeData().getGameMode() == GameModes.CREATIVE) {
                    return;
                }

                player.sendMessage(
                        ChatTypes.SYSTEM,
                        "You find yourself unable to place that block."
                );
            }
            event.setCancelled(true);
        }
    }

    public int getLevel(Location location) {

        // Not in Wilderness
        if (!isApplicable(location.getExtent())) {
            return 0;
        }

        // In Wilderness
        return Math.max(0, Math.max(Math.abs(location.getBlockX()), Math.abs(location.getBlockZ())) / 500) + 1;
    }

    public int getOreMod(int level) {
        double modifier = Math.max(1, (level * 3));

        Optional<ModifierService> optService = game.getServiceManager().provide(ModifierService.class);
        if (optService.isPresent()) {
            ModifierService service = optService.get();
            if (service.isActive(Modifiers.DOUBLE_WILD_ORES)) {
                modifier *= 2;
            }
        }

        return (int) modifier;
    }

    private void addPool(Location block, int fortune, boolean hasSilkTouch) {

        BlockType blockType = block.getType();

        Collection<ItemStack> generalDrop = DropRegistry.createDropsFor(game, blockType, hasSilkTouch);
        if (DropRegistry.dropsSelf(blockType)) {
            fortune = 0;
        }

        final int times = Probability.getRandom(getOreMod(getLevel(block)));
        ItemFountain fountain = new ItemFountain(
                game,
                toWorld.from(block.getExtent()),
                block.getPosition().add(.5, 0, .5),
                new FixedIntGenerator(fortune),
                generalDrop
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

        TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(fountain, times);
        Task task = game.getSyncScheduler().runRepeatingTaskAfter(plugin, runnable, 20, 20).get();
        runnable.setTask(task);
    }
}
