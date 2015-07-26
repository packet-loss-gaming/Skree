/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.generator.FixedIntGenerator;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.item.ItemFountain;
import com.skelril.nitro.modifier.ModifierFunctions;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.block.DropRegistry;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.droptable.CofferResolver;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spongepowered.api.Game;
import org.spongepowered.api.attribute.Attributes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.manipulator.AttributeData;
import org.spongepowered.api.data.manipulator.entity.ExplosiveRadiusData;
import org.spongepowered.api.data.manipulator.entity.HealthData;
import org.spongepowered.api.data.manipulator.item.EnchantmentData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
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
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import static com.skelril.skree.content.registry.TypeCollections.ore;
import static com.skelril.skree.content.registry.item.CustomItemTypes.RED_FEATHER;
import static java.util.concurrent.TimeUnit.SECONDS;

public class WildernessWorldWrapper extends WorldEffectWrapperImpl implements Runnable {

    private SkreePlugin plugin;
    private Game game;

    private DropTable dropTable;

    private Map<Player, Integer> playerLevelMap = new WeakHashMap<>();

    public WildernessWorldWrapper(SkreePlugin plugin, Game game) {
        this(plugin, game, new ArrayList<>());
    }

    public WildernessWorldWrapper(SkreePlugin plugin, Game game, Collection<World> worlds) {
        super("Wilderness", worlds);
        this.plugin = plugin;
        this.game = game;

        ItemStackBuilder builder = game.getRegistry().getItemBuilder();
        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller(ModifierFunctions.ADD);
        dropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(new CofferResolver(game, 10), 12)
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                builder.reset().itemType((ItemType) RED_FEATHER).quantity(1).build()
                                                        )
                                                ), 100000
                                        )
                                )
                        )
                )
        );

        game.getScheduler().getTaskBuilder().execute(this).interval(1, SECONDS).submit(plugin);
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

                if (max <= 80) { // TODO do this a better way, but for now it prevents super mobs

                    double newMax = max * getHealthMod(level);

                    health.setMaxHealth(newMax);
                    health.setHealth(newMax);

                    entity.offer(health);
                }
            }

            Optional<AttributeData> attributeData = entity.getData(AttributeData.class);
            if (attributeData.isPresent()) {
                AttributeData attributes = attributeData.get();
                attributes.setBase(Attributes.GENERIC_ATTACK_DAMAGE, getDamageMod(level));

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
            int level = getLevel(loc);

            Collection<ItemStack> drops = dropTable.getDrops(
                    level,
                    getDropMod(
                            level,
                            ((Monster) entity).getHealthData().getMaxHealth()
                    )
            );

            int times = 1;

            Optional<ModifierService> optService = game.getServiceManager().provide(ModifierService.class);
            if (optService.isPresent()) {
                ModifierService service = optService.get();
                if (service.isActive(Modifiers.DOUBLE_WILD_DROPS)) {
                    times *= 2;
                }
            }

            ItemDropper dropper = new ItemDropper(game, toWorld.from(loc.getExtent()), loc.getPosition());
            for (int i = 0; i < times; ++i) {
                dropper.dropItems(drops);
            }

            // TODO needs updated XP API
            // event.setExp(event.getExp() * level);
        }
    }

    @Subscribe
    public void onBlockBreak(EntityBreakBlockEvent event) {
        Location loc = event.getBlock();
        if (!isApplicable(loc.getExtent())) return;

        BlockType type = loc.getBlockType();
        if (ore().contains(type)) {
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
                        BlockType blockType = loc.getBlockType();
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
            BlockType type = loc.getBlockType();
            if (ore().contains(type)) {
                addPool(loc, 0, false);
            }
        }
    }

    @Subscribe
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock();
        if (!isApplicable(loc.getExtent())) return;
        if (ore().contains(loc.getBlockType())) {

            if (event instanceof PlayerPlaceBlockEvent) {
                Player player = ((PlayerPlaceBlockEvent) event).getEntity();

                // Allow creative mode players to still place blocks
                if (player.getGameModeData().getGameMode() == GameModes.CREATIVE) {
                    return;
                }

                try {
                    Vector3d origin = loc.getPosition();
                    World world = toWorld.from(loc.getExtent());
                    for (int i = 0; i < 40; ++i) {
                        ParticleEffect effect = game.getRegistry().getParticleEffectBuilder(
                                ParticleTypes.CRIT_MAGIC
                        ).motion(
                                new Vector3d(
                                        Probability.getRangedRandom(-1, 1),
                                        Probability.getRangedRandom(-.7, .7),
                                        Probability.getRangedRandom(-1, 1)
                                )
                        ).count(1).build();

                        world.spawnParticles(effect, origin.add(.5, .5, .5));
                    }

                } catch (Exception ex) {
                    TextBuilder builder = Texts.builder().color(TextColors.RED).append(
                            Texts.of("You find yourself unable to place that block.")
                    );
                    player.sendMessage(/* ChatTypes.SYSTEM, */builder.build());
                }
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

    public double getDropMod(int level, double mobHealth) {
        return (level * .2) + (mobHealth * .04);
    }

    public int getHealthMod(int level) {
        return level > 1 ? level : 1;
    }

    public int getDamageMod(int level) {
        return level > 1 ? (level - 1) * 2 : 0;
    }

    public int getOreMod(int level) {
        int modifier = Math.max(1, level * 3);

        Optional<ModifierService> optService = game.getServiceManager().provide(ModifierService.class);
        if (optService.isPresent()) {
            ModifierService service = optService.get();
            if (service.isActive(Modifiers.DOUBLE_WILD_ORES)) {
                modifier *= 2;
            }
        }

        return modifier;
    }

    private void addPool(Location block, int fortune, boolean hasSilkTouch) {

        BlockType blockType = block.getBlockType();

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
        Task task = game.getScheduler().getTaskBuilder().execute(runnable).delay(1, SECONDS).interval(
                1,
                SECONDS
        ).submit(plugin);
        runnable.setTask(task);
    }

    @Override
    public void run() {
        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities(p -> p.getType() == EntityTypes.PLAYER)) {
                int currentLevel = getLevel(entity.getLocation());
                int lastLevel = playerLevelMap.getOrDefault(entity, -1);
                if (currentLevel != lastLevel) {
                    ((Player) entity).sendTitle(
                            new Title(
                                    Texts.of("Wilderness Level"),
                                    Texts.of(currentLevel),
                                    20,
                                    0,
                                    20,
                                    false,
                                    false
                            )
                    );
                    playerLevelMap.put((Player) entity, currentLevel);
                }
            }
        }
    }
}
