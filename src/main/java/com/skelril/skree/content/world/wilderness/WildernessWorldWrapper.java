/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.data.util.AttributeUtil;
import com.skelril.nitro.data.util.EnchantmentUtil;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.item.ItemFountain;
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
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
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
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
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
import static com.skelril.skree.content.registry.block.CustomBlockTypes.GRAVE_STONE;
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

        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller((a, b) -> (int) (a + b));
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
        List<Entity> entities = event.getEntities();

        for (Entity entity : entities) {
            Location<World> loc = entity.getLocation();
            Optional<Integer> optLevel = getLevel(loc);

            if (!optLevel.isPresent()) continue;
            int level = optLevel.get();

            if (level > 1) {
                // TODO move damage modification
                if (entity instanceof Monster) {
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
                } else if (entity instanceof Arrow) {
                    // Handles cases of both blocks and entities
                    if (!(((Arrow) entity).getShooter() instanceof Player)) {
                        ((EntityArrow) entity).setDamage(((EntityArrow) entity).getDamage() + getDamageMod(level));
                    }
                }
            }

            Optional<Value<Integer>> optExplosiveRadius = Optional.empty();
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
    }

    @Listener
    public void onEntityAttack(InteractEntityEvent event) {
        Entity entity = event.getTargetEntity();

        Optional<Integer> optLevel = getLevel(entity.getLocation());

        if (!optLevel.isPresent()) {
            return;
        }

        int level = optLevel.get();
        if (!allowsPvP(level)) {
            return;
        }

        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            event.setCancelled(true);
            return;
        }

        Optional<Projectile> optProjectile = event.getCause().first(Projectile.class);
        if (optProjectile.isPresent()) {
            ProjectileSource source = optProjectile.get().getShooter();
            if (source instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onEntityDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();

        Location<World> loc = entity.getLocation();
        Optional<Integer> optLevel = getLevel(loc);

        if (!optLevel.isPresent()) {
            return;
        }
        int level = optLevel.get();

        if (entity instanceof Monster) {
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

            ItemDropper dropper = new ItemDropper(loc);
            for (int i = 0; i < times; ++i) {
                dropper.dropItems(drops);
            }
        }
        GRAVE_STONE.createGraveFromDeath(event);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {

        Entity entity = null;

        Optional<?> rootCause = event.getCause().root();
        if (rootCause.isPresent() && rootCause.get() instanceof Entity) {
            entity = (Entity) rootCause.get();
        }

        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
        for (Transaction<BlockSnapshot> block : transactions) {
            BlockSnapshot original = block.getOriginal();
            Optional<Location<World>> optLoc = original.getLocation();

            if (!optLoc.isPresent()) {
                continue;
            }

            Optional<Integer> optLevel = getLevel(optLoc.get());

            if (!optLevel.isPresent()) {
                continue;
            }

            int level = optLevel.get();
            Location<World> loc = optLoc.get();

            BlockType type = original.getState().getType();
            if (ore().contains(type)) {
                int fortuneMod = 0;
                boolean silkTouch = false;

                if (entity instanceof ArmorEquipable) {
                    Optional<ItemStack> held = ((ArmorEquipable) entity).getItemInHand();
                    if (held.isPresent()) {
                        ItemStack stack = held.get();

                        // TODO Currently abusing NMS to determine "breakability"
                        ItemType itemType = stack.getItem();
                        if (itemType instanceof Item && type instanceof Block) {
                            if (!((Item) stack.getItem()).canHarvestBlock((Block) type)) {
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

                addPool(loc, type, fortuneMod, silkTouch);
            } else if (entity instanceof Player && type.equals(BlockTypes.STONE) && Probability.getChance(Math.max(12, 100 - level))) {
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
                game.getScheduler().createTaskBuilder().delayTicks(1).execute(() -> {
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
    public void onEntityHarvest(HarvestEntityEvent event) {
        Location<World> entityLoc = event.getTargetEntity().getLocation();

        Optional<Integer> optLevel = getLevel(entityLoc);
        if (!optLevel.isPresent()) {
            return;
        }
        int level = optLevel.get();

        event.setExperience(Math.max(event.getExperience(), event.getOriginalExperience() * level));
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
        for (Transaction<BlockSnapshot> block : transactions) {
            Optional<Location<World>> optLoc = block.getFinal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                continue;
            }

            Location<World> loc = optLoc.get();
            Optional<?> rootCause = event.getCause().root();
            if (rootCause.isPresent() && rootCause.get() instanceof Player && ore().contains(loc.getBlockType())) {
                Player player = (Player) rootCause.get();

                // Allow creative mode players to still place blocks
                if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                    continue;
                }

                try {
                    Vector3d origin = loc.getPosition();
                    World world = loc.getExtent();
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

                block.setValid(false);
            }
        }
    }

    public Optional<Integer> getLevel(Location<World> location) {

        // Not in Wilderness
        if (!isApplicable(location)) {
            return Optional.empty();
        }

        // In Wilderness
        return Optional.of(Math.max(0, Math.max(Math.abs(location.getBlockX()), Math.abs(location.getBlockZ())) / 500) + 1);
    }

    public boolean allowsPvP(int level) {
        return level > 5;
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

    private void addPool(Location<World> block, BlockType blockType, int fortune, boolean hasSilkTouch) {

        Optional<Integer> optLevel = getLevel(block);
        Validate.isTrue(optLevel.isPresent());
        int level = optLevel.get();

        Collection<ItemStack> generalDrop = createDropsFor(blockType, hasSilkTouch);
        if (DropRegistry.dropsSelf(blockType)) {
            fortune = 0;
        }

        final int times = Probability.getRandom(getOreMod(level));
        final int finalFortune = fortune;
        ItemFountain fountain = new ItemFountain(
                new Location<>(block.getExtent(), block.getPosition().add(.5, 0, .5)),
                (a) -> finalFortune,
                generalDrop
        ) {
            @Override
            public boolean run(int timesL) {
                getExtent().playSound(
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
                getExtent().playSound(SoundTypes.BLAZE_DEATH, getPos(), .2F, 0);
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
                int currentLevel = getLevel(entity.getLocation()).get();
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
