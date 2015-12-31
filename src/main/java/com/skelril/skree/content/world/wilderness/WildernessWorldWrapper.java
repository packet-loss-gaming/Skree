/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.data.util.EnchantmentUtil;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.entity.EntityHealthPrinter;
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
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.text.title.TitleBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.content.registry.TypeCollections.ore;
import static com.skelril.skree.content.registry.block.CustomBlockTypes.GRAVE_STONE;
import static com.skelril.skree.content.registry.item.CustomItemTypes.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class WildernessWorldWrapper extends WorldEffectWrapperImpl implements Runnable {

    private DropTable dropTable;

    private Map<Player, Integer> playerLevelMap = new WeakHashMap<>();

    public WildernessWorldWrapper() {
        this(new ArrayList<>());
    }

    public WildernessWorldWrapper(Collection<World> worlds) {
        super("Wilderness", worlds);

        SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller((a, b) -> (int) (a + b));
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
                                                                newItemStack((ItemType) SCROLL_OF_SUMMATION)
                                                        )
                                                ), 2000
                                        ),
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

        Task.builder().execute(this).interval(1, SECONDS).submit(SkreePlugin.inst());
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
    public void onPlayerCombat(DamageEntityEvent event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Living)) {
            return;
        }

        Optional<Integer> optLevel = getLevel(entity.getLocation());
        if (!optLevel.isPresent()) {
            return;
        }

        int level = optLevel.get();
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            Entity srcEntity;
            if (optDamageSource.isPresent() && optDamageSource.get() instanceof IndirectEntityDamageSource) {
                srcEntity = ((IndirectEntityDamageSource) optDamageSource.get()).getIndirectSource();
            } else {
                srcEntity = optDamageSource.get().getSource();
            }

            if (!(srcEntity instanceof Living)) {
                return;
            }

            Living living = (Living) srcEntity;
            if (entity instanceof Player && living instanceof Player) {
                processPvP(level, (Player) living, (Player) entity, event);
            } else if (entity instanceof Player) {
                processMonsterAttack(level, living, (Player) entity, event);
            } else if (living instanceof Player) {
                processPlayerAttack(level, (Player) living, (Living) entity, event);
            }
        }
    }

    private void processPvP(int level, Player attacker, Player defender, DamageEntityEvent event) {
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

        attacker.sendMessage(Texts.of(TextColors.RED, "PvP is opt-in only in this part of the Wilderness!"));
        attacker.sendMessage(Texts.of(TextColors.RED, "Mandatory PvP is from level ", getFirstPvPLevel(), " and on."));

        event.setCancelled(true);
    }

    private void processMonsterAttack(int level, Living attacker, Player defender, DamageEntityEvent event) {
        event.setBaseDamage(event.getBaseDamage() + Probability.getRandom(level) - 1);
    }

    private final EntityHealthPrinter healthPrinter = new EntityHealthPrinter(
            Optional.of(
                    Texts.of(
                            TextColors.DARK_AQUA,
                            "Entity Health: ",
                            Texts.placeholder("health int"),
                            " / ",
                            Texts.placeholder("max health int")
                    )
            ),
            Optional.of(Texts.of(TextColors.GOLD, TextStyles.BOLD, "KO!"))
    );

    private void processPlayerAttack(int level, Player attacker, Living defender, DamageEntityEvent event) {
        Task.builder().delayTicks(1).execute(
                () -> healthPrinter.print(MessageSinks.to(Collections.singleton(attacker)), defender)
        ).submit(SkreePlugin.inst());
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

            Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
            if (optService.isPresent()) {
                ModifierService service = optService.get();
                if (service.isActive(Modifiers.DOUBLE_WILD_DROPS)) {
                    times *= 2;
                }
            }

            ItemDropper dropper = new ItemDropper(loc);
            for (int i = 0; i < times; ++i) {
                dropper.dropItems(drops, Cause.of(this));
            }
        }
        GRAVE_STONE.createGraveFromDeath(event);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Object srcObj = event.getCause().get(NamedCause.SOURCE).orElse(null);
        if (!(srcObj instanceof Entity)) {
            return;
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

                if (srcObj instanceof ArmorEquipable) {
                    Optional<ItemStack> held = ((ArmorEquipable) srcObj).getItemInHand();
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
                    } else if (srcObj instanceof Player) {
                        continue;
                    }
                }

                addPool(loc, type, fortuneMod, silkTouch);
            } else if (srcObj instanceof Player && type.equals(BlockTypes.STONE) && Probability.getChance(Math.max(12, 100 - level))) {
                Vector3d max = loc.getPosition().add(1, 1, 1);
                Vector3d min = loc.getPosition().sub(1, 1, 1);

                Extent world = loc.getExtent();

                if (Probability.getChance(3)) {
                    Optional<Entity> optEntity = world.createEntity(EntityTypes.SILVERFISH, loc.getPosition().add(.5, 0, .5));
                    if (optEntity.isPresent()) {
                        world.spawnEntity(optEntity.get(), Cause.of(this));
                    }
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
                                    world.setBlockType(x, y, z, BlockTypes.MONSTER_EGG);
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
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        // Workaround for SpongeCommon#374
        if (transactions.size() > 1) {
            return;
        }

        for (Transaction<BlockSnapshot> block : transactions) {
            Optional<Location<World>> optLoc = block.getFinal().getLocation();

            if (!optLoc.isPresent() || !isApplicable(optLoc.get())) {
                continue;
            }

            Location<World> loc = optLoc.get();
            Optional<Player> optPlayer = event.getCause().first(Player.class);
            if (optPlayer.isPresent() && ore().contains(loc.getBlockType())) {
                Player player = optPlayer.get();

                // Allow creative mode players to still place blocks
                if (player.getGameModeData().type().get().equals(GameModes.CREATIVE)) {
                    continue;
                }

                try {
                    Vector3d origin = loc.getPosition();
                    World world = loc.getExtent();
                    for (int i = 0; i < 40; ++i) {
                        ParticleEffect effect = ParticleEffect.builder().type(
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

    public int getFirstPvPLevel() {
        return 6;
    }

    public boolean allowsPvP(int level) {
        return level >= getFirstPvPLevel();
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

        Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
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
        return DropRegistry.createDropsFor(blockType, hasSilkTouch);
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
                generalDrop,
                Cause.of(this)
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
        Task task = Task.builder().execute(runnable).delay(1, SECONDS).interval(
                1,
                SECONDS
        ).submit(SkreePlugin.inst());
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
