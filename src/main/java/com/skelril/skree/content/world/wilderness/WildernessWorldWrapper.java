/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.data.util.EnchantmentUtil;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.entity.EntityHealthPrinter;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.numeric.MathExt;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ItemStackConfig;
import com.skelril.nitro.registry.dynamic.QuantityBoundedItemStackConfig;
import com.skelril.nitro.text.CombinedText;
import com.skelril.nitro.text.PlaceHolderText;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.droptable.CofferResolver;
import com.skelril.skree.content.modifier.Modifiers;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.content.world.wilderness.wanderer.Fangz;
import com.skelril.skree.content.world.wilderness.wanderer.GraveDigger;
import com.skelril.skree.content.world.wilderness.wanderer.StormBringer;
import com.skelril.skree.content.world.wilderness.wanderer.WanderingBoss;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.*;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Boss;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.monster.Wither;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.Egg;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.Extent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.content.registry.TypeCollections.ore;
import static com.skelril.skree.content.registry.block.CustomBlockTypes.GRAVE_STONE;
import static com.skelril.skree.content.registry.item.CustomItemTypes.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class WildernessWorldWrapper extends WorldEffectWrapperImpl implements Runnable {
    private WildernessConfig config;

    private DropTable commonDropTable;
    private DropTable netherMobDropTable;

    private Map<UUID, WildernessPlayerMeta> playerMetaMap = new HashMap<>();

    private WanderingMobManager wanderingMobManager;

    public WildernessWorldWrapper(WildernessConfig config) {
        this(config, new ArrayList<>());
    }

    public WildernessWorldWrapper(WildernessConfig config, Collection<World> worlds) {
        super("Wilderness", worlds);
        this.config = config;

        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller((a, b) -> (int) (a + b));
        commonDropTable = new MasterDropTable(
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
                                                                newItemStack((ItemType) SCROLL_OF_SUMMATION)
                                                        )
                                                ), 2000
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:two_tailed_sword")
                                                        )
                                                ), 10000
                                        )
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
        netherMobDropTable = new MasterDropTable(
                slipRoller,
                Lists.newArrayList(
                        commonDropTable,
                        new DropTableImpl(
                                slipRoller,
                                Lists.newArrayList(
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack("skree:nether_bow")
                                                        )
                                                ), 10000
                                        ),
                                        new DropTableEntryImpl(
                                                new SimpleDropResolver(
                                                        Lists.newArrayList(
                                                                newItemStack((ItemType) NETHER_BOWL)
                                                        )
                                                ), 20000
                                        )
                                )
                        )
                )
        );

        Task.builder().execute(this).interval(1, SECONDS).submit(SkreePlugin.inst());

        setupWanderers();
    }

    private void setupWanderers() {
        Map<String, WanderingBoss<? extends Entity>> wanderers = new HashMap<>();
        wanderers.put("fangz", new Fangz());
        wanderers.put("grave_digger", new GraveDigger());
        wanderers.put("storm_bringer", new StormBringer());

        wanderingMobManager = new WanderingMobManager(wanderers);
    }

    public WanderingMobManager getWanderingMobManager() {
        return wanderingMobManager;
    }

    @Override
    public void addWorld(World world) {
        super.addWorld(world);
        tf(world).setAllowedSpawnTypes(true, true);
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        List<Entity> entities = event.getEntities();

        Optional<BlockSpawnCause> optBlockCause = event.getCause().first(BlockSpawnCause.class);
        for (Entity entity : entities) {
            Location<World> loc = entity.getLocation();
            Optional<Integer> optLevel = getLevel(loc);

            if (!optLevel.isPresent()) continue;
            int level = optLevel.get();

            if (entity instanceof Egg && optBlockCause.isPresent()) {
                PrimedTNT explosive = (PrimedTNT) entity.getLocation().getExtent().createEntity(
                        EntityTypes.PRIMED_TNT,
                        entity.getLocation().getPosition()
                );

                explosive.setVelocity(entity.getVelocity());
                explosive.offer(Keys.FUSE_DURATION, 20 * 4);

                // TODO used to have a 1/4 chance of creating fire
                entity.getLocation().getExtent().spawnEntity(
                        explosive, Cause.source(SpawnCause.builder().type(SpawnTypes.DISPENSE).build()).build()
                );

                event.setCancelled(true);
                return;
            }

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

                    // Wandering Bosses
                    Collection<String> wanderers = wanderingMobManager.getSupportedWanderersOfType(entity.getType());
                    for (String wanderer: wanderers) {
                        if (wanderingMobManager.chanceBind(wanderer, level, entity)) {
                            break;
                        }
                    }
                }
            }

            Optional<Value<Integer>> optExplosiveRadius = Optional.empty();
            // Optional<Value<Integer>> optExplosiveRadius = event.getEntity().getValue(Keys.EXPLOSIVE_RADIUS);

            if (optExplosiveRadius.isPresent()) {
                Value<Integer> explosiveRadius = optExplosiveRadius.get();
                int min = explosiveRadius.get();

                entity.offer(
                        Keys.EXPLOSION_RADIUS,
                        Optional.of(MathExt.bound((min + level) / 2, min, entity instanceof Fireball ? 4 : 9))
                );
            }
        }
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event) {
        if (isApplicable(event.getToTransform().getExtent())) {
            Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
            if (optWorldService.isPresent()) {
                Collection<World> worlds = optWorldService.get().getEffectWrapper(MainWorldWrapper.class).get().getWorlds();
                event.setToTransform(new Transform<>(worlds.iterator().next().getSpawnLocation()));
            }
        }
    }

    private final EntityHealthPrinter healthPrinter = new EntityHealthPrinter(
            CombinedText.of(
                    TextColors.DARK_AQUA,
                    "Entity Health: ",
                    new PlaceHolderText("health int"),
                    " / ",
                    new PlaceHolderText("max health int")
            ),
            CombinedText.of(TextColors.GOLD, TextStyles.BOLD, "KO!")
    );


    private PlayerCombatParser createFor(Cancellable event, int level) {
        return new PlayerCombatParser() {
            @Override
            public void processPvP(Player attacker, Player defender) {
                if (allowsPvP(level)) {
                    return;
                }

                Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
                if (optService.isPresent()) {
                    PvPService service = optService.get();
                    if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
                        return;
                    }
                }

                attacker.sendMessage(Text.of(TextColors.RED, "PvP is opt-in only in this part of the Wilderness!"));
                attacker.sendMessage(Text.of(TextColors.RED, "Mandatory PvP is from level ", getFirstPvPLevel(), " and on."));

                event.setCancelled(true);
            }

            @Override
            public void processMonsterAttack(Living attacker, Player defender) {
                if (!(event instanceof DamageEntityEvent)) {
                    return;
                }

                DamageEntityEvent dEvent = (DamageEntityEvent) event;
                // If they're endermites they hit through armor, otherwise they get a damage boost
                if (attacker.getType() == EntityTypes.ENDERMITE) {
                    for (Tuple<DamageModifier, Function<? super Double, Double>> modifier : dEvent.getModifiers()) {
                        dEvent.setDamage(modifier.getFirst(), (a) -> 0D);
                    }

                    dEvent.setBaseDamage(1);
                } else {
                    dEvent.setBaseDamage(dEvent.getBaseDamage() + getDamageMod(level));
                }

                // Only apply scoring while in survival mode
                if (defender.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL) != GameModes.SURVIVAL) {
                    return;
                }

                WildernessPlayerMeta meta = playerMetaMap.get(defender.getUniqueId());
                if (meta != null) {
                    meta.hit();
                }
            }

            @Override
            public void processPlayerAttack(Player attacker, Living defender) {
                Task.builder().delayTicks(1).execute(
                        () -> healthPrinter.print(MessageChannel.fixed(attacker), defender)
                ).submit(SkreePlugin.inst());

                if (!(defender instanceof Monster) || defender instanceof Creeper) {
                    return;
                }

                // Only apply scoring while in survival mode
                if (attacker.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL) != GameModes.SURVIVAL) {
                    return;
                }

                WildernessPlayerMeta meta = playerMetaMap.get(attacker.getUniqueId());
                if (meta != null) {
                    meta.attack();

                    if (meta.getRatio() > 30 && meta.getFactors() > 35) {
                        Deque<Entity> spawned = new ArrayDeque<>();
                        for (int i = Probability.getRandom(5); i > 0; --i) {
                            Entity entity = attacker.getWorld().createEntity(
                                    EntityTypes.ENDERMITE,
                                    defender.getLocation().getPosition()
                            );

                            entity.getWorld().spawnEntity(
                                    entity,
                                    Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                            );
                            spawned.add(entity);
                        }

                        IntegratedRunnable runnable = new IntegratedRunnable() {
                            @Override
                            public boolean run(int times) {
                                Entity mob = spawned.poll();
                                if (mob.isLoaded() && mob.getWorld().equals(attacker.getWorld())) {
                                    mob.setLocation(attacker.getLocation());
                                }
                                return true;
                            }

                            @Override
                            public void end() {

                            }
                        };

                        TimedRunnable timedRunnable = new TimedRunnable<>(runnable, spawned.size());

                        timedRunnable.setTask(Task.builder().execute(
                                timedRunnable
                        ).delayTicks(40).intervalTicks(20).submit(SkreePlugin.inst()));
                    }

                    if (System.currentTimeMillis() - meta.getLastReset() >= TimeUnit.MINUTES.toMillis(5)) {
                        meta.reset();
                    }
                }
            }
        };
    }

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        Optional<Integer> optLevel = getLevel(event.getTargetEntity().getLocation());
        if (!optLevel.isPresent()) {
            return;
        }

        createFor(event, optLevel.get()).parse(event);
    }

    @Listener
    public void onPlayerCombat(CollideEntityEvent.Impact event) {
        Optional<Projectile> optProjectile = event.getCause().first(Projectile.class);
        if (!optProjectile.isPresent()) {
            return;
        }

        Optional<Integer> optLevel = getLevel(optProjectile.get().getLocation());
        if (!optLevel.isPresent()) {
            return;
        }

        createFor(event, optLevel.get()).parse(event);
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
            DropTable dropTable;

            if (entity.getLocation().getExtent().getDimension() == DimensionTypes.NETHER || entity instanceof Wither) {
                dropTable = netherMobDropTable;
            } else {
                dropTable = commonDropTable;
            }

            Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
            if (optDamageSource.isPresent()) {
                Entity srcEntity;
                if (optDamageSource.get() instanceof IndirectEntityDamageSource) {
                    srcEntity = ((IndirectEntityDamageSource) optDamageSource.get()).getIndirectSource();
                } else {
                    srcEntity = optDamageSource.get().getSource();
                }

                int dropTier = level;

                if (srcEntity instanceof Player) {
                    Optional<ItemStack> optHeldItem = ((Player) srcEntity).getItemInHand(HandTypes.MAIN_HAND);
                    if (optHeldItem.isPresent()) {
                        Optional<ItemEnchantment> optLooting = EnchantmentUtil.getHighestEnchantment(
                                optHeldItem.get(),
                                Enchantments.LOOTING
                        );

                        if (optLooting.isPresent()) {
                            dropTier += optLooting.get().getLevel();
                        }
                    }

                    dropTier = getDropTier(dropTier);

                    Collection<ItemStack> drops = dropTable.getDrops(
                            (entity instanceof Boss ? 5 : 1) * dropTier,
                            getDropMod(
                                    dropTier,
                                    ((Monster) entity).getHealthData().maxHealth().get(),
                                    entity.getType()
                            )
                    );

                    int times = 1;

                    Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
                    if (optService.isPresent()) {
                        ModifierService service = optService.get();
                        if (service.isActive(Modifiers.DOUBLE_WILD_DROPS)) {
                            times *= 2;
                        }
                    }

                    ItemDropper dropper = new ItemDropper(loc);
                    for (int i = 0; i < times; ++i) {
                        dropper.dropStacks(drops, SpawnTypes.DROPPED_ITEM);
                    }
                }
            }

            if (entity.getType() == EntityTypes.ENDERMITE && Probability.getChance(20)) {
                entity.getWorld().triggerExplosion(
                        Explosion.builder()
                                .location(entity.getLocation())
                                .shouldBreakBlocks(true)
                                .radius(4F)
                                .build(),
                        Cause.source(SkreePlugin.container()).build()
                );
            }
        }
        GRAVE_STONE.createGraveFromDeath(event);
    }

    private Set<Location<World>> markedOrePoints = new HashSet<>();

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Optional<Entity> optSrcEnt = event.getCause().get(NamedCause.SOURCE, Entity.class);
        if (!optSrcEnt.isPresent()) {
            return;
        }

        Entity srcEnt = optSrcEnt.get();

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

            BlockState state = original.getState();
            BlockType type = state.getType();

            // Prevent item dupe glitch by removing the position before subsequent breaks
            markedOrePoints.remove(loc);
            if (config.getDropAmplificationConfig().amplifies(state)) {
                markedOrePoints.add(loc);
            }

            if (srcEnt instanceof Player && type.equals(BlockTypes.STONE) && Probability.getChance(Math.max(12, 250 - level))) {
                Vector3d max = loc.getPosition().add(1, 1, 1);
                Vector3d min = loc.getPosition().sub(1, 1, 1);

                Extent world = loc.getExtent();

                if (Probability.getChance(3)) {
                    Entity entity = world.createEntity(EntityTypes.SILVERFISH, loc.getPosition().add(.5, 0, .5));
                    world.spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.BLOCK_SPAWNING).build()).build());
                }

                // Do this one tick later to guarantee no collision with transaction data
                Task.builder().delayTicks(1).execute(() -> {
                    for (int x = min.getFloorX(); x <= max.getFloorX(); ++x) {
                        for (int z = min.getFloorZ(); z <= max.getFloorZ(); ++z) {
                            for (int y = min.getFloorY(); y <= max.getFloorY(); ++y) {
                                if (!world.containsBlock(x, y, z)) {
                                    continue;
                                }

                                if (world.getBlockType(x, y, z) == BlockTypes.STONE) {
                                    world.setBlockType(
                                            x,
                                            y,
                                            z,
                                            BlockTypes.MONSTER_EGG,
                                            BlockChangeFlag.NONE,
                                            Cause.source(SkreePlugin.container()).build()
                                    );
                                }
                            }
                        }
                    }
                }).submit(SkreePlugin.inst());
            }
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        Optional<Player> optPlayer = event.getCause().get(NamedCause.SOURCE, Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            for (Transaction<BlockSnapshot> block :  event.getTransactions()) {
                Optional<Location<World>> optLoc = block.getFinal().getLocation();

                if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                    continue;
                }

                Location<World> loc = optLoc.get();
                BlockState finalState = block.getFinal().getState();
                if (config.getDropAmplificationConfig().amplifies(finalState)) {
                    // Allow creative mode players to still place blocks
                    if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                        continue;
                    }

                    BlockType originalType = block.getOriginal().getState().getType();
                    if (ore().contains(originalType)) {
                        continue;
                    }

                    try {
                        Vector3d origin = loc.getPosition();
                        World world = loc.getExtent();
                        for (int i = 0; i < 40; ++i) {
                            ParticleEffect effect = ParticleEffect.builder().type(
                                    ParticleTypes.MAGIC_CRITICAL_HIT
                            ).velocity(
                                    new Vector3d(
                                            Probability.getRangedRandom(-1, 1),
                                            Probability.getRangedRandom(-.7, .7),
                                            Probability.getRangedRandom(-1, 1)
                                    )
                            ).quantity(1).build();

                            world.spawnParticles(effect, origin.add(.5, .5, .5));
                        }
                    } catch (Exception ex) {
                        player.sendMessage(
                            /* ChatTypes.SYSTEM, */
                                Text.of(
                                        TextColors.RED,
                                        "You find yourself unable to place that block."
                                )
                        );
                    }

                    block.setValid(false);
                }
            }
        }
    }

    private ItemStackSnapshot getPoolItemDrop(ItemStackSnapshot snapshot) {
        Map<String, QuantityBoundedItemStackConfig> replacementMapping = config.getDropAmplificationConfig().getItemReplacementMapping();
        ItemStackConfig replacementItem = replacementMapping.get(snapshot.getType().getId());
        if (replacementItem != null) {
            return ((ItemStack) (Object) replacementItem.toNSMStack()).createSnapshot();
        }

        return snapshot;
    }

    @Listener
    public void onItemDrop(DropItemEvent.Destruct event) {
        Optional<BlockSpawnCause> optSpawnCause = event.getCause().get(NamedCause.SOURCE, BlockSpawnCause.class);
        if (!optSpawnCause.isPresent()) {
            return;
        }

        BlockSpawnCause spawnCause = optSpawnCause.get();
        BlockSnapshot blockSnapshot = spawnCause.getBlockSnapshot();

        Optional<Location<World>> optLocation = blockSnapshot.getLocation();
        if (!optLocation.isPresent()) {
            return;
        }

        Location<World> loc = optLocation.get();
        if (!markedOrePoints.remove(loc)) {
            return;
        }

        Optional<Integer> optLevel = getLevel(loc);
        if (!optLevel.isPresent()) {
            return;
        }

        List<ItemStackSnapshot> itemStacks = new ArrayList<>();
        event.getEntities().forEach((entity -> {
            if (entity instanceof Item) {
                ItemStackSnapshot snapshot = ((Item) entity).item().get();
                itemStacks.add(getPoolItemDrop(snapshot));
            }
        }));

        addPool(loc, () -> itemStacks);
    }

    public Set<Map.Entry<Player, WildernessPlayerMeta>> getMetaInformation() {
        Set<Map.Entry<Player, WildernessPlayerMeta>> resultSets = new HashSet<>();
        for (Map.Entry<UUID, WildernessPlayerMeta> entry : playerMetaMap.entrySet()) {
            Optional<Player> optPlayer = Sponge.getServer().getPlayer(entry.getKey());
            if (!optPlayer.isPresent()) {
                continue;
            }

            Player player = optPlayer.get();
            if (!player.isOnline()) {
                continue;
            }

            resultSets.add(new AbstractMap.SimpleEntry<>(player, entry.getValue()));
        }
        return resultSets;
    }

    public Optional<Integer> getLevel(Location<World> location) {

        // Not in Wilderness
        if (!isApplicable(location)) {
            return Optional.empty();
        }

        // In Wilderness
        return Optional.of(
                Math.max(
                        0,
                        Math.max(
                                Math.abs(location.getBlockX()),
                                Math.abs(location.getBlockZ())) / getLevelUnit(location.getExtent()
                        )
                ) + 1
        );
    }

    public int getLevelUnit(World world) {
        return 500;
    }

    public int getFirstPvPLevel() {
        return 6;
    }

    public boolean allowsPvP(int level) {
        return level >= getFirstPvPLevel();
    }

    public int getDropTier(int level) {
        return Math.min(level, 30);
    }

    public double getDropMod(int dropTier) {
        return getDropMod(dropTier, null, null);
    }

    public double getDropMod(int dropTier, @Nullable Double mobHealth, @Nullable EntityType entityType) {
        double modifier = (dropTier * .2) + (mobHealth != null ? mobHealth * .04 : 0);
        if (entityType != null) {
            if (entityType == EntityTypes.WITHER || entityType == EntityTypes.CREEPER) {
                modifier *= 5;
            } else if (entityType == EntityTypes.SILVERFISH) {
                modifier *= 2;
            } else if (entityType == EntityTypes.ENDERMITE) {
                modifier *= .1;
            }
        }
        return modifier;
    }

    public int getHealthMod(int level) {
        return level > 1 ? level : 1;
    }

    public int getDamageMod(int level) {
        return level > 1 ? (level - 1) * 2 : 0;
    }

    public int getOreMod(int dropTier) {
        int modifier = (int) Math.round(Math.max(1, dropTier * 1.5));

        Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
        if (optService.isPresent()) {
            ModifierService service = optService.get();
            if (service.isActive(Modifiers.DOUBLE_WILD_ORES)) {
                modifier *= 2;
            }
        }

        return modifier;
    }

    private void addPool(Location<World> block, Supplier<Collection<ItemStackSnapshot>> itemStackSupplier) {
        Optional<Integer> optLevel = getLevel(block);
        Validate.isTrue(optLevel.isPresent());
        int level = optLevel.get();

        int times = Probability.getRandom(getOreMod(getDropTier(level)));
        WildernessDropPool dropPool = new WildernessDropPool(block, itemStackSupplier, times);

        TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(dropPool, times);
        Task task = Task.builder().execute(runnable).delay(1, SECONDS).interval(
                1,
                SECONDS
        ).submit(SkreePlugin.inst());
        runnable.setTask(task);
    }

    @Override
    public void run() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            int currentLevel = getLevel(player.getLocation()).orElse(-1);
            WildernessPlayerMeta meta = playerMetaMap.getOrDefault(player.getUniqueId(), new WildernessPlayerMeta());
            int lastLevel = meta.getLevel();

            // Always set the level so as to mark the player meta as relevant
            // if it is -1 no time stamp update shall be performed
            meta.setLevel(currentLevel);

            // Display a title change, unless the current level is -1 (outside of the Wilderness)
            if (currentLevel != -1 && currentLevel != lastLevel) {
                TextColor color = (allowsPvP(currentLevel) ? TextColors.RED : TextColors.WHITE);
                player.sendTitle(
                        Title.builder()
                                .title(Text.of(color, "Wilderness Level"))
                                .subtitle(Text.of(color, currentLevel))
                                .fadeIn(20)
                                .fadeOut(20)
                                .build()
                );
                playerMetaMap.putIfAbsent(player.getUniqueId(), meta);
            }
        }

        playerMetaMap.entrySet().removeIf(entry ->
                System.currentTimeMillis() - entry.getValue().getLastChange() >= TimeUnit.MINUTES.toMillis(5)
        );
    }
}
