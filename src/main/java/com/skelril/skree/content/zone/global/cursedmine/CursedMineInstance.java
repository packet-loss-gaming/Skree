/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BlockID;
import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.position.PositionRandomizer;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.content.zone.global.cursedmine.curse.*;
import com.skelril.skree.content.zone.global.cursedmine.hitlist.HitList;
import com.skelril.skree.content.zone.global.cursedmine.restoration.BlockRecord;
import com.skelril.skree.content.zone.global.cursedmine.restoration.PlayerMappedBlockRecordIndex;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.animal.Wolf;
import org.spongepowered.api.entity.living.monster.Blaze;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class CursedMineInstance extends LegacyZoneBase implements Runnable {

  private static final ItemType[] ITEMS = new ItemType[] {
      ItemTypes.IRON_BLOCK, ItemTypes.IRON_ORE, ItemTypes.IRON_INGOT,
      ItemTypes.GOLD_BLOCK, ItemTypes.GOLD_ORE, ItemTypes.GOLD_INGOT, ItemTypes.GOLD_NUGGET,
      ItemTypes.REDSTONE_ORE, ItemTypes.REDSTONE,
      ItemTypes.LAPIS_BLOCK, ItemTypes.LAPIS_ORE, ItemTypes.DYE,
      ItemTypes.DIAMOND_BLOCK, ItemTypes.DIAMOND_ORE, ItemTypes.DIAMOND,
      ItemTypes.EMERALD_BLOCK, ItemTypes.EMERALD_ORE, ItemTypes.EMERALD
  };

  private ZoneBoundingBox floodGate;
  private Location<World> entryPoint;

  private final long timeTilPumpShutoff = 18000;
  private long lastActivation = 0;
  private PlayerMappedBlockRecordIndex recordSystem = new PlayerMappedBlockRecordIndex();
  private Map<Player, List<Task>> activeTask = new HashMap<>();
  private HitList hitList;

  private int idleTicks = 0;
  private int ticks = 0;

  public CursedMineInstance(ZoneRegion region, HitList hitList) {
    super(region);
    this.hitList = hitList;
  }

  @Override
  public boolean init() {
    remove();
    setUp();
    return true;
  }

  @Override
  public void forceEnd() {
    revertAll();
    clearCurses();
    remove(getPlayers(PARTICIPANT));
  }

  @Override
  public Clause<Player, ZoneStatus> add(Player player) {
    player.setLocation(entryPoint);
    return new Clause<>(player, ZoneStatus.ADDED);
  }

  private void setUp() {
    Vector3i offset = getRegion().getMinimumPoint();

    entryPoint = new Location<>(getRegion().getExtent(), offset.getX() + 71.5, offset.getY() + 59, offset.getZ() + 86.5);

    floodGate = new ZoneBoundingBox(offset.add(66, 40, 131), new Vector3i(13, 1, 9));
  }

  public void revertAll() {
    recordSystem.revertAll();
  }

  public void randomRestore() {
    recordSystem.revertByTime(Probability.getRangedRandom(9000, 60000));
  }

  public void revertPlayer(Player player) {
    recordSystem.revertByPlayer(player.getUniqueId());
  }

  public boolean recordBlockBreak(Player player, BlockRecord record) {
    return recordSystem.addItem(player.getUniqueId(), record);
  }

  public boolean hasRecordForPlayer(Player player) {
    return recordSystem.hasRecordForPlayer(player.getUniqueId());
  }

  public boolean hasrecordForPlayerAt(Player player, Location<World> location) {
    return recordSystem.hasRecordForPlayerAt(player.getUniqueId(), location);
  }

  public HitList getHitList() {
    return hitList;
  }

  public void activatePumps() {
    lastActivation = System.currentTimeMillis();
  }

  public void clearCurses(Player player) {
    List<Task> activeTasks = activeTask.remove(player);
    if (activeTasks != null) {
      activeTasks.forEach(Task::cancel);
    }
  }

  private void clearCurses() {
    activeTask.values().forEach(a -> a.forEach(Task::cancel));
    activeTask.clear();
  }

  @Override
  public void run() {
    ++ticks;
    ++idleTicks;
    if (!isEmpty()) {
      idleTicks = 0;
      changeWater();
    } else if (idleTicks > 60 * 5) {
      expire();
      return;
    }

    if (ticks % 4 == 0) {
      drain();
      sweepFloor();
      randomRestore();
    }
  }

  public void eatFood(Player player) {
    if (player.get(Keys.SATURATION).orElse(0D) - 1 >= 0) {
      player.offer(Keys.SATURATION, player.get(Keys.SATURATION).get() - 1);
    } else if (player.get(Keys.FOOD_LEVEL).orElse(0) - 1 >= 0) {
      player.offer(Keys.FOOD_LEVEL, player.get(Keys.FOOD_LEVEL).get() - 1);
    } else if (player.get(Keys.HEALTH).orElse(0D) - 1 >= 0) {
      player.offer(Keys.HEALTH, player.get(Keys.HEALTH).get() - 1);
    }
  }

  public void poison(Player player, int duration) {
    if (Probability.getChance(player.getLocation().getBlockY() / 2)) {
      PotionEffect posionEffect = PotionEffect.of(PotionEffectTypes.POISON, 2, 20 * duration);

      List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
      potionEffects.add(posionEffect);
      player.offer(Keys.POTION_EFFECTS, potionEffects);

      player.sendMessage(Text.of(TextColors.RED, "The ore releases a toxic gas poisoning you!"));
    }
  }

  public void ghost(final Player player, BlockType blockID) {
    if (Probability.getChance(player.getLocation().getBlockY())) {
      if (Probability.getChance(2)) {
        switch (Probability.getRandom(6)) {
          case 1:
            player.sendMessage(Text.of(TextColors.YELLOW, "Caspher the friendly ghost drops some bread."));
            new ItemDropper(player.getLocation()).dropStacks(
                Lists.newArrayList(newItemStack(ItemTypes.BREAD, Probability.getRandom(16)))
            );
            break;
          case 2:
            player.sendMessage(Text.of(TextColors.YELLOW, "COOKIE gives you a cookie."));
            new ItemDropper(player.getLocation()).dropStacks(
                Lists.newArrayList(newItemStack(ItemTypes.COOKIE))
            );
            break;
          case 3:
            player.sendMessage(Text.of(TextColors.YELLOW, "Caspher the friendly ghost appears."));
            List<ItemStack> caspherLoot = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
              caspherLoot.add(newItemStack(ItemTypes.IRON_INGOT, Probability.getRandom(64)));
              caspherLoot.add(newItemStack(ItemTypes.GOLD_INGOT, Probability.getRandom(64)));
              caspherLoot.add(newItemStack(ItemTypes.DIAMOND, Probability.getRandom(64)));
            }

            new ItemDropper(player.getLocation()).dropStacks(caspherLoot);
            break;
          case 4:
            player.sendMessage(Text.of(TextColors.YELLOW, "John gives you a new jacket."));
            new ItemDropper(player.getLocation()).dropStacks(
                Lists.newArrayList(newItemStack(ItemTypes.LEATHER_CHESTPLATE))
            );
            break;
          case 5:
            player.sendMessage(Text.of(TextColors.YELLOW, "Tim teleports items to you."));
            getContained(Item.class).forEach(i -> i.setLocation(player.getLocation()));

            // Add in some extra drops just in case the loot wasn't very juicy
            List<ItemStack> teleportLootExtras = Lists.newArrayList(
                newItemStack(ItemTypes.IRON_INGOT, Probability.getRandom(64)),
                newItemStack(ItemTypes.GOLD_INGOT, Probability.getRandom(64)),
                newItemStack(ItemTypes.DIAMOND, Probability.getRandom(64))
            );
            new ItemDropper(player.getLocation()).dropStacks(teleportLootExtras);
            break;
          case 6:
            player.sendMessage(Text.of(TextColors.YELLOW, "Dan gives you a sparkling touch."));

            ItemType sparkingType;
            switch (Probability.getRandom(3)) {
              case 1:
                sparkingType = ItemTypes.IRON_INGOT;
                break;
              case 2:
                sparkingType = ItemTypes.GOLD_INGOT;
                break;
              case 3:
                sparkingType = ItemTypes.DIAMOND;
                break;
              default:
                sparkingType = ItemTypes.REDSTONE;
                break;
            }

            TimedRunnable inventoryFX = new TimedRunnable<>(new IntegratedRunnable() {
              @Override
              public boolean run(int times) {
                new InventoryCurse(sparkingType, 64).accept(player);
                return true;
              }

              @Override
              public void end() {

              }
            }, 10);

            inventoryFX.setTask(
                Task.builder().execute(
                    inventoryFX
                ).interval(
                    500, TimeUnit.MILLISECONDS
                ).submit(SkreePlugin.inst())
            );

            activeTask.merge(player, Lists.newArrayList(inventoryFX.getTask()), (a, b) -> {
              a.addAll(b);
              return a;
            });

            break;
          default:
            break;
        }
      } else {
        switch (Probability.getRandom(11)) {
          case 1:
            if (Probability.getChance(4)) {
              if (blockID == BlockTypes.DIAMOND_ORE) {
                hitList.addPlayer(player);
                player.sendMessage(Text.of(TextColors.RED, "You ignite fumes in the air!"));
                EditSession ess = WorldEdit.getInstance().getEditSessionFactory().getEditSession(
                    new WorldResolver(getRegion().getExtent()).getWorldEditWorld(),
                    -1
                );

                try {
                  Vector3d pos = player.getLocation().getPosition();

                  ess.fillXZ(
                      new Vector(pos.getX(), pos.getY(), pos.getZ()),
                      WorldEdit.getInstance().getBaseBlockFactory().getBaseBlock(BlockID.FIRE),
                      20,
                      20,
                      true
                  );
                } catch (MaxChangedBlocksException ignored) {

                }
                for (int i = Probability.getRandom(24) + 20; i > 0; --i) {
                  final boolean untele = i == 11;
                  Task.builder().execute(() -> {
                    if (untele) {
                      recordSystem.revertByPlayer(player.getUniqueId());
                      hitList.remPlayer(player);
                    }

                    if (!contains(player)) {
                      return;
                    }

                    Vector3i pos = new PositionRandomizer(3).createPosition3i(player.getLocation().getPosition());

                    player.getLocation().getExtent().triggerExplosion(
                        Explosion.builder()
                            .radius(3)
                            .location(player.getLocation().setPosition(pos.toDouble().add(.5, .5, .5)))
                            .shouldDamageEntities(true)
                            .canCauseFire(true)
                            .build()
                    );
                  }).delayTicks(12 * i).submit(SkreePlugin.inst());
                }
              } else {
                hitList.addPlayer(player);
                // player.chat("Who's a good ghost?!?!");
                Task.builder().execute(() -> {
                  // player.chat("Don't hurt me!!!");
                  Task.builder().execute(() -> {
                    // player.chat("Nooooooooooo!!!");
                    TimedRunnable cannonFX = new TimedRunnable<>(new IntegratedRunnable() {
                      @Override
                      public boolean run(int times) {
                        new CannonCurse().accept(player);
                        return true;
                      }

                      @Override
                      public void end() {

                      }
                    }, 120);

                    cannonFX.setTask(
                        Task.builder().execute(
                            cannonFX
                        ).interval(
                            500, TimeUnit.MILLISECONDS
                        ).submit(SkreePlugin.inst())
                    );

                    activeTask.merge(player, Lists.newArrayList(cannonFX.getTask()), (a, b) -> {
                      a.addAll(b);
                      return a;
                    });
                  }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
                }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
              }
              break;
            }
          case 2:
            player.sendMessage(Text.of(TextColors.RED, "Dave attaches to your soul..."));
            for (int i = 20; i > 0; --i) {
              Task.builder().execute(() -> {
                if (!contains(player)) {
                  return;
                }

                player.offer(Keys.HEALTH, Probability.getRandom(Probability.getRandom(player.get(Keys.MAX_HEALTH).get())) - 1);
              }).delayTicks(12 * i).submit(SkreePlugin.inst());
            }
            break;
          case 3:
            player.sendMessage(Text.of(TextColors.RED, "George plays with fire, sadly too close to you."));
            TimedRunnable fireFX = new TimedRunnable<>(new IntegratedRunnable() {
              @Override
              public boolean run(int times) {
                new FireCurse().accept(player);
                return true;
              }

              @Override
              public void end() {

              }
            }, 90);

            fireFX.setTask(
                Task.builder().execute(
                    fireFX
                ).interval(
                    500, TimeUnit.MILLISECONDS
                ).submit(SkreePlugin.inst())
            );

            activeTask.merge(player, Lists.newArrayList(fireFX.getTask()), (a, b) -> {
              a.addAll(b);
              return a;
            });
            break;
          case 4:
            player.sendMessage(Text.of(TextColors.RED, "Simon says pick up sticks."));
            List<ItemStack> sticks = new ArrayList<>();
            for (int i = 0; i < player.getInventory().size() * 1.5; i++) {
              sticks.add(newItemStack(ItemTypes.STICK, 64));
            }
            new ItemDropper(player.getLocation()).dropStacks(sticks);
            break;
          case 5:
            player.sendMessage(Text.of(TextColors.RED, "Ben dumps out your backpack."));
            TimedRunnable butterFingerFX = new TimedRunnable<>(new IntegratedRunnable() {
              @Override
              public boolean run(int times) {
                new ButterFingersCurse().accept(player);
                return true;
              }

              @Override
              public void end() {

              }
            }, 20);

            butterFingerFX.setTask(
                Task.builder().execute(
                    butterFingerFX
                ).interval(
                    500, TimeUnit.MILLISECONDS
                ).submit(SkreePlugin.inst())
            );

            activeTask.merge(player, Lists.newArrayList(butterFingerFX.getTask()), (a, b) -> {
              a.addAll(b);
              return a;
            });
            break;
          case 6:
            player.sendMessage(Text.of(TextColors.RED, "Merlin attacks with a mighty rage!"));
            TimedRunnable merlinFX = new TimedRunnable<>(new IntegratedRunnable() {
              @Override
              public boolean run(int times) {
                new MerlinCurse().accept(player);
                return true;
              }

              @Override
              public void end() {

              }
            }, 40);

            merlinFX.setTask(
                Task.builder().execute(
                    merlinFX
                ).interval(
                    500, TimeUnit.MILLISECONDS
                ).submit(SkreePlugin.inst())
            );

            activeTask.merge(player, Lists.newArrayList(merlinFX.getTask()), (a, b) -> {
              a.addAll(b);
              return a;
            });
            break;
          case 7:
            player.sendMessage(Text.of(TextColors.RED, "Dave tells everyone that your mining!"));
            MessageChannel.TO_PLAYERS.send(Text.of(
                TextColors.GOLD, player.getName() + " is mining in the cursed mine!!!"
            ));
            break;
          case 8:
            player.sendMessage(Text.of(TextColors.RED, "Dave likes your food."));
            hitList.addPlayer(player);
            TimedRunnable starvationFX = new TimedRunnable<>(new IntegratedRunnable() {
              @Override
              public boolean run(int times) {
                new StarvationCurse().accept(player);
                return true;
              }

              @Override
              public void end() {

              }
            }, 40);

            starvationFX.setTask(
                Task.builder().execute(
                    starvationFX
                ).interval(
                    500, TimeUnit.MILLISECONDS
                ).submit(SkreePlugin.inst())
            );

            activeTask.merge(player, Lists.newArrayList(starvationFX.getTask()), (a, b) -> {
              a.addAll(b);
              return a;
            });
            break;
          case 9:
            player.sendMessage(Text.of(TextColors.RED, "Hallow declares war on YOU!"));
            for (int i = 0; i < Probability.getRangedRandom(10, 30); i++) {
              Blaze blaze = (Blaze) getRegion().getExtent().createEntity(EntityTypes.BLAZE, player.getLocation().getPosition());
              blaze.setTarget(player);
              getRegion().getExtent().spawnEntity(blaze);
            }
            break;
          case 10:
            player.sendMessage(Text.of(TextColors.RED, "A legion of hell hounds appears!"));
            for (int i = 0; i < Probability.getRangedRandom(10, 30); i++) {
              Wolf wolf = (Wolf) getRegion().getExtent().createEntity(EntityTypes.WOLF, player.getLocation().getPosition());
              wolf.setTarget(player);
              getRegion().getExtent().spawnEntity(wolf);
            }
            break;
          case 11:
            if (blockID == BlockTypes.EMERALD_ORE) {
              player.sendMessage(Text.of(TextColors.RED, "Dave got a chemistry set!"));
              hitList.addPlayer(player);
              TimedRunnable deadlyPotionFX = new TimedRunnable<>(new IntegratedRunnable() {
                @Override
                public boolean run(int times) {
                  new DeadlyPotionCurse().accept(player);
                  return true;
                }

                @Override
                public void end() {

                }
              }, 2 * 60 * 30);

              deadlyPotionFX.setTask(
                  Task.builder().execute(
                      deadlyPotionFX
                  ).interval(
                      500, TimeUnit.MILLISECONDS
                  ).submit(SkreePlugin.inst())
              );

              activeTask.merge(player, Lists.newArrayList(deadlyPotionFX.getTask()), (a, b) -> {
                a.addAll(b);
                return a;
              });
            } else {
              player.sendMessage(Text.of(TextColors.RED, "Dave says hi, that's not good."));
              hitList.addPlayer(player);
              TimedRunnable attackOfDaveFX = new TimedRunnable<>(new IntegratedRunnable() {
                @Override
                public boolean run(int times) {
                  new AttackOfDaveCurse().accept(player);
                  return true;
                }

                @Override
                public void end() {

                }
              }, 2 * 60 * 30);

              attackOfDaveFX.setTask(
                  Task.builder().execute(
                      attackOfDaveFX
                  ).interval(
                      500, TimeUnit.MILLISECONDS
                  ).submit(SkreePlugin.inst())
              );

              activeTask.merge(player, Lists.newArrayList(attackOfDaveFX.getTask()), (a, b) -> {
                a.addAll(b);
                return a;
              });
            }
            break;
          default:
            break;
        }
      }
    }
  }

  private static Set<BlockType> replaceableTypes = new HashSet<>();

  static {
    replaceableTypes.add(BlockTypes.WATER);
    replaceableTypes.add(BlockTypes.FLOWING_WATER);
    replaceableTypes.add(BlockTypes.PLANKS);
    replaceableTypes.add(BlockTypes.AIR);
  }

  private void changeWater() {
    BlockType targetType = BlockTypes.AIR;
    if (lastActivation == 0 || System.currentTimeMillis() - lastActivation >= timeTilPumpShutoff) {
      targetType = BlockTypes.PLANKS;
    }

    final BlockType finalTarget = targetType;
    floodGate.forAll((pt) -> {
      if (replaceableTypes.contains(getRegion().getExtent().getBlockType(pt))) {
        getRegion().getExtent().setBlockType(pt, finalTarget);
      }
    });
  }

  private boolean checkInventory(Player player) {
    QueryOperation[] operations = (QueryOperation[]) Arrays.stream(ITEMS).map(QueryOperationTypes.ITEM_TYPE::of).toArray();
    return player.getInventory().query(operations).peek().isPresent();
  }

  private static ItemStack LAPIS_DYE;

  static {
    DyeableData data = Sponge.getDataManager().getManipulatorBuilder(DyeableData.class).get().create();
    data.set(Keys.DYE_COLOR, DyeColors.BLUE);
    LAPIS_DYE = newItemStack(ItemTypes.DYE, data);
    LAPIS_DYE.setQuantity(-1);
  }

  private List<Clause<QueryOperation<?>, Integer>>  getItemRemovalQueries() {
    List<Clause<QueryOperation<?>, Integer>> queryNumToRemove = new ArrayList<>();

    // Iron
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.IRON_BLOCK), 2
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.IRON_ORE), 4
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.IRON_INGOT), 8
    ));

    // Gold
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.GOLD_BLOCK), 2
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.GOLD_ORE), 4
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.GOLD_INGOT), 10
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.GOLD_NUGGET), 80
    ));

    // Redstone
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.REDSTONE_ORE), 2
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.REDSTONE), 34
    ));

    // Lap
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.LAPIS_BLOCK), 2
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.LAPIS_ORE), 4
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(LAPIS_DYE), 34
    ));

    // Diamond
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.DIAMOND_BLOCK), 2
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.DIAMOND_ORE), 4
    ));
    queryNumToRemove.add(new Clause<>(
        QueryOperationTypes.ITEM_TYPE.of(ItemTypes.DIAMOND), 16
    ));

    return queryNumToRemove;
  }

  public void drain() {
    List<Clause<QueryOperation<?>, Integer>> itemRemovalQueries = getItemRemovalQueries();

    for (Entity e : getContained(Carrier.class)) {
      // TODO Inventory API not fully implemented
      if (!(e instanceof Player)) {
        continue;
      }

      Inventory eInventory = ((Carrier) e).getInventory();

      if (e instanceof Player) {
        Location<World> playerLoc = e.getLocation();

        // Emerald
        long diff = System.currentTimeMillis() - lastActivation;
        if (playerLoc.getY() < 30 && (lastActivation == 0 || diff <= timeTilPumpShutoff * .35 || diff >= timeTilPumpShutoff * 5)) {
          PositionRandomizer randomizer = new PositionRandomizer(5);
          for (int i = 0; i < Probability.getRangedRandom(2, 5); i++) {
            Vector3i targetPos;
            do {
              targetPos = randomizer.createPosition3i(playerLoc.getPosition());
            } while (getRegion().getExtent().getBlockType(targetPos) != BlockTypes.AIR);

            Entity entity = getRegion().getExtent().createEntity(EntityTypes.BLAZE, targetPos);
            getRegion().getExtent().spawnEntity(entity);
          }
        }
      }

      for (int i = 0; i < (eInventory.size() / 2) - 2 || i < 1; i++) {
        if (e instanceof Player) {
          if (Probability.getChance(15) && checkInventory((Player) e)) {
            ((Player) e).sendMessage(
                Text.of(TextColors.YELLOW, "Divine intervention protects some of your items.")
            );
            continue;
          }
        }

        itemRemovalQueries.forEach((clause) -> {
          QueryOperation<?> itemQuery = clause.getKey();
          int numberToRemove = Probability.getRandom(clause.getValue());

          eInventory.query(itemQuery).poll(numberToRemove);
        });
      }
    }
  }

  public void sweepFloor() {

    for (Item item : getContained(Item.class)) {

      if (!contains(item)) {
        continue;
      }

      ItemType id = item.getItemType();
      for (ItemType aItem : ITEMS) {
        if (aItem == id) {
          ItemStackSnapshot snapshot = item.get(Keys.REPRESENTED_ITEM).get();
          int newAmt = (int) (snapshot.getQuantity() * .8);
          if (newAmt < 1) {
            item.remove();
          } else {
            ItemStack newStack = snapshot.createStack();
            newStack.setQuantity(newAmt);
            item.offer(Keys.REPRESENTED_ITEM, newStack.createSnapshot());
          }
          break;
        }
      }
    }
  }
}
