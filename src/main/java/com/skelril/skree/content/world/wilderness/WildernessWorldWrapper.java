/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.skelril.nitro.data.util.AttributeUtil;
import com.skelril.nitro.data.util.EnchantmentUtil;
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
import com.skelril.nitro.registry.block.MultiTypeRegistry;
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
import org.spongepowered.api.block.BlockTransaction;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.BreakBlockEvent;
import org.spongepowered.api.event.block.HarvestBlockEvent;
import org.spongepowered.api.event.block.PlaceBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.TitleBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.TypeCollections.ore;
import static com.skelril.skree.content.registry.item.CustomItemTypes.RED_FEATHER;
import static com.skelril.skree.content.registry.item.CustomItemTypes.RED_SHARD;
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

        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller(ModifierFunctions.ADD);
        dropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(new CofferResolver(10), 12)
                                )
                        ),
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack((ItemType) RED_FEATHER)
                                                        )
                                                ), 100000
                                        )
                                )
                        )
                )
        );

        game.getScheduler().createTaskBuilder().execute(this).interval(1, SECONDS).submit(plugin);
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        Entity entity = event.getTargetEntity();

        if (!isApplicable(entity.getWorld())) return;

        Location loc = entity.getLocation();

        final int level = getLevel(loc);

        if (entity instanceof Monster && level > 1) {
            HealthData healthData = ((Monster) entity).getHealthData();
            double curMax = healthData.maxHealth().get();

            if (curMax <= 80) { // TODO do this a better way, but for now it prevents super mobs

                double newMax = curMax * getHealthMod(level);

                healthData.set(Keys.MAX_HEALTH, newMax);
                healthData.set(Keys.HEALTH, newMax);

                entity.offer(healthData);
            }

            if (AttributeUtil.respectsGenericAttackDamage(entity)) {
                AttributeUtil.setGenericAttackDamage(
                        entity,
                        getDamageMod(level) + AttributeUtil.getGenericAttackDamage(entity)
                );
            }
        }

        Optional<Value<Integer>> optExplosiveRadius = Optional.absent();
        // Optional<Value<Integer>> optExplosiveRadius = event.getEntity().getValue(Keys.EXPLOSIVE_RADIUS);

        if (optExplosiveRadius.isPresent()) {
            Value<Integer> explosiveRadius = optExplosiveRadius.get();
            int min = explosiveRadius.get();

            entity.offer(
                    Keys.EXPLOSIVE_RADIUS,
                    Math.min(
                            entity instanceof Fireball ? 4 : 9,
                            Math.max(min, (min + level) / 2)
                    )
            );
        }
    }

    @Listener
    public void onEntityDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();

        if (!isApplicable(entity.getWorld())) return;

        Location loc = entity.getLocation();

        if (entity instanceof Monster) {
            int level = getLevel(loc);

            Collection<ItemStack> drops = dropTable.getDrops(
                    level,
                    getDropMod(
                            level,
                            ((Monster) entity).getHealthData().maxHealth().get()
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
        }
    }

    @Listener
    public void onBlockBreak(BreakBlockEvent event) {

        Optional<?> rootCause = event.getCause().root();

        if (!(rootCause.isPresent() && rootCause.get() instanceof Entity)) return;

        Entity entity = (Entity) rootCause.get();

        List<BlockTransaction> transactions = event.getTransactions();
        for (BlockTransaction block : transactions) {
            Optional<Location<World>> optLoc = block.getOriginal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                continue;
            }

            Location loc = optLoc.get();

            BlockType type = loc.getBlockType();
            if (ore().contains(type)) {
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
                                continue;
                            }
                        }

                        // Handle fortune
                        Optional<ItemEnchantment> optFortune = EnchantmentUtil.getHighestEnchantment(
                                stack,
                                Enchantments.FORTUNE
                        );
                        if (optFortune.isPresent()) {
                            fortuneMod = optFortune.get().getLevel();
                        }

                        // Handle silk touch
                        Optional<ItemEnchantment> optSilkTouch = EnchantmentUtil.getHighestEnchantment(
                                stack,
                                Enchantments.SILK_TOUCH
                        );
                        if (optSilkTouch.isPresent()) {
                            silkTouch = true;
                        }
                    } else if (entity instanceof Player) {
                        continue;
                    }
                }

                addPool(loc, fortuneMod, silkTouch);
            } else if (type.equals(BlockTypes.STONE) && Probability.getChance(Math.max(12, 100 - getLevel(loc)))) {
                Vector3d max = loc.getPosition().add(1, 1, 1);
                Vector3d min = loc.getPosition().sub(1, 1, 1);

                Extent world = loc.getExtent();

                if (Probability.getChance(3)) {
                    Optional<Entity> optEntity = world.createEntity(EntityTypes.SILVERFISH, loc.getPosition().add(.5, 0, .5));
                    if (optEntity.isPresent()) {
                        world.spawnEntity(optEntity.get(), Cause.empty());
                    }
                }

                // Do this one tick later to guarantee no collision with transaction data
                game.getScheduler().createTaskBuilder().delay(1).execute(() -> {
                    for (int x = min.getFloorX(); x <= max.getFloorX(); ++x) {
                        for (int z = min.getFloorZ(); z <= max.getFloorZ(); ++z) {
                            for (int y = min.getFloorY(); y <= max.getFloorY(); ++y) {
                                if (!world.containsBlock(x, y, z)) {
                                    continue;
                                }

                                if (world.getBlockType(x, y, z) == BlockTypes.STONE) {
                                    world.setBlockType(x, y, z, BlockTypes.MONSTER_EGG);
                                }
                            }
                        }
                    }
                }).submit(plugin);

            }
        }
    }

    @Listener
    public void onBlockHarvest(HarvestBlockEvent event) {
        Optional<Location<World>> optBlockLoc = event.getTargetBlock().getLocation();

        if (!optBlockLoc.isPresent()) {
            return;
        }
        
        Location blockLoc = optBlockLoc.get();

        if (!isApplicable(blockLoc.getExtent())) {
            return;
        }

        event.setExperience(Math.max(event.getExperience(), event.getOriginalExperience() * getLevel(blockLoc)));
    }

    @Listener
    public void onEntityHarvest(HarvestEntityEvent event) {
        Location entityLoc = event.getTargetEntity().getLocation();

        if (!isApplicable(entityLoc.getExtent())) {
            return;
        }

        event.setExperience(Math.max(event.getExperience(), event.getOriginalExperience() * getLevel(entityLoc)));
    }

    @Listener
    public void onExplode(ExplosionEvent.Detonate event) {
        List<BlockTransaction> transactions = event.getTransactions();
        for (BlockTransaction block : transactions) {
            Optional<Location<World>> optLoc = block.getOriginal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                continue;
            }

            Location loc = optLoc.get();

            BlockType type = loc.getBlockType();
            if (ore().contains(type)) {
                addPool(loc, 0, false);
            }
        }
    }

    @Listener
    public void onBlockPlace(PlaceBlockEvent event) {
        List<BlockTransaction> transactions = event.getTransactions();
        for (BlockTransaction block : transactions) {
            Optional<Location<World>> optLoc = block.getOriginal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get().getExtent())) {
                continue;
            }

            Location loc = optLoc.get();

            if (ore().contains(loc.getBlockType())) {
                Optional<?> rootCause = event.getCause().root();
                if (rootCause.isPresent() && rootCause.get() instanceof Player) {
                    Player player = (Player) rootCause.get();

                    // Allow creative mode players to still place blocks
                    if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                        continue;
                    }

                    try {
                        Vector3d origin = loc.getPosition();
                        World world = toWorld.from(loc.getExtent());
                        for (int i = 0; i < 40; ++i) {
                            ParticleEffect effect = game.getRegistry().createParticleEffectBuilder(
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
                        player.sendMessage(
                                /* ChatTypes.SYSTEM, */
                                Texts.of(
                                        TextColors.RED,
                                        "You find yourself unable to place that block."
                                )
                        );
                    }
                }
                event.setCancelled(true);
            }
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

    public Collection<ItemStack> createDropsFor(BlockType blockType, boolean hasSilkTouch) {
        if (!hasSilkTouch && MultiTypeRegistry.isRedstoneOre(blockType)) {
            return Lists.newArrayList(newItemStack((ItemType) RED_SHARD));
        }
        return DropRegistry.createDropsFor(game, blockType, hasSilkTouch);
    }

    private void addPool(Location block, int fortune, boolean hasSilkTouch) {

        BlockType blockType = block.getBlockType();

        Collection<ItemStack> generalDrop = createDropsFor(blockType, hasSilkTouch);
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
                        1
                );
                return super.run(timesL);
            }

            @Override
            public void end() {
                getWorld().playSound(SoundTypes.BLAZE_DEATH, getPos(), .2F, 0);
            }
        };

        TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(fountain, times);
        Task task = game.getScheduler().createTaskBuilder().execute(runnable).delay(1, SECONDS).interval(
                1,
                SECONDS
        ).submit(plugin);
        runnable.setTask(task);
    }

    @Override
    public void run() {
        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities(p -> p.getType().equals(EntityTypes.PLAYER))) {
                int currentLevel = getLevel(entity.getLocation());
                int lastLevel = playerLevelMap.getOrDefault(entity, -1);
                if (currentLevel != lastLevel) {
                    ((Player) entity).sendTitle(
                            new TitleBuilder()
                                    .title(Texts.of("Wilderness Level"))
                                    .subtitle(Texts.of(currentLevel))
                                    .fadeIn(20)
                                    .fadeOut(20)
                                    .build()
                    );
                    playerLevelMap.put((Player) entity, currentLevel);
                }
            }
        }
    }
}
