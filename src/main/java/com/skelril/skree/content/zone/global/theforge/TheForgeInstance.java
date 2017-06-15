/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.item.FixedPointItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static com.skelril.skree.content.modifier.Modifiers.HEXA_FACTORY_SPEED;
import static com.skelril.skree.content.modifier.Modifiers.TRIPLE_FACTORY_PRODUCTION;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class TheForgeInstance extends LegacyZoneBase implements Runnable {
  private final TheForgeConfig config;
  private ForgeState state;

  private Location<World> centralDropPoint;

  public TheForgeInstance(ZoneRegion region, TheForgeConfig config) {
    super(region);
    this.config = config;
  }

  private void setUp() {
    Vector3d centerPoint = getRegion().getCenter();
    centralDropPoint = new Location<>(
        getRegion().getExtent(),
        new Vector3d(
            centerPoint.getX(),
            getRegion().getMinimumPoint().getY() + 11,
            centerPoint.getZ()
        )
    );

    state = ForgeState.load();
  }

  @Override
  public boolean init() {
    remove();
    setUp();
    return true;
  }

  private Optional<ItemStack> getResultingItemStack(ItemStackSnapshot snapshot) {
    if (!config.isCompatibleWith(snapshot.createStack())) {
      return Optional.empty();
    }

    net.minecraft.item.ItemStack result = FurnaceRecipes.instance().getSmeltingResult(tf(snapshot.createStack()));
    if (result == null) {
      return Optional.empty();
    }

    return Optional.of(tf(result));
  }

  private int getQuantityToSupply(ItemStackSnapshot snapshot) {
    int total = 0;

    for (int i = 0; i < snapshot.getCount(); ++i) {
      total += Probability.getRandom(8);
    }

    Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
    if (optService.isPresent() && optService.get().isActive(TRIPLE_FACTORY_PRODUCTION)) {
      total *= 3;
    }

    return total;
  }

  private void addResource(ItemStackSnapshot snapshot) {
    Optional<ItemStack> optResult = getResultingItemStack(snapshot);
    if (!optResult.isPresent()) {
      return;
    }

    state.getResults().merge(optResult.get(), getQuantityToSupply(snapshot), (a, b) -> a + b);
  }

  private void runOreCheck() {
    for (Item item : getContained(Item.class)) {
      BlockType belowType = item.getLocation().add(0, -1, 0).getBlockType();
      if (belowType == BlockTypes.GOLD_BLOCK) {
        addResource(item.item().get());
        item.remove();
      }
    }

    state.save();
  }

  private void killPlayersInLava() {
    for (Player player : getPlayers(PARTICIPANT)) {
      if (player.getLocation().getBlockType() != BlockTypes.LAVA) {
        continue;
      }

      if (player.get(Keys.HEALTH).get() <= 0) {
        continue;
      }

      player.offer(Keys.HEALTH, 0D);
    }
  }

  private int getQuantityToProduce() {
    int max = getPlayers(PARTICIPANT).size() * 9;

    Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
    if (optService.isPresent() && optService.get().isActive(HEXA_FACTORY_SPEED)) {
      max *= 6;
    }

    return Probability.getRangedRandom(max / 3, max);
  }

  private List<ItemStack> getProduce() {
    Map<ItemStack, Integer> results = state.getResults();
    int quantityToProduce = getQuantityToProduce();

    List<ItemStack> produce = new ArrayList<>();
    while (!results.isEmpty() && quantityToProduce > 0) {
      ItemStack stackToMake = Probability.pickOneOf(results.keySet());
      int supply = results.get(stackToMake);
      if (supply >= quantityToProduce) {
        results.put(stackToMake, supply - quantityToProduce);

        for (int i = 0; i < quantityToProduce; ++i) {
          produce.add(newItemStack(stackToMake));
        }

        quantityToProduce = 0;
      } else {
        results.remove(stackToMake);

        for (int i = 0; i < supply; ++i) {
          produce.add(newItemStack(stackToMake));
        }

        quantityToProduce = quantityToProduce - supply;
      }
    }

    return produce;
  }

  private final List<Vector3d> pointAdjustments = Lists.newArrayList(
      new Vector3d(-.5, 0, 0),
      new Vector3d(.5, 0, 0),
      new Vector3d(0, 0, -.5),
      new Vector3d(0, 0, .5)
  );

  private void dropResults() {
    Location<World> targetDropPoint = centralDropPoint.add(Probability.pickOneOf(pointAdjustments));

    new FixedPointItemDropper(targetDropPoint).dropStacks(getProduce(), SpawnTypes.PLUGIN);

    state.save();
  }

  private static final List<EntityType> POSSIBLE_MOBS = Lists.newArrayList(
      EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.CREEPER, EntityTypes.SPIDER
  );

  private static Cause getSpawnCause() {
    return Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).owner(SkreePlugin.container()).build();
  }

  private void summonMobs() {
    if (getContained().size() > 50) {
      return;
    }

    List<Entity> entities = new ArrayList<>();

    for (int i = Probability.getRandom(getPlayers(PARTICIPANT).size() * 15); i > 0; --i) {
      Entity e = getRegion().getExtent().createEntity(Probability.pickOneOf(POSSIBLE_MOBS), getRandomEntryPoint().getPosition());
      if (e instanceof Skeleton) {
        ((Skeleton) e).setItemInHand(HandTypes.MAIN_HAND, newItemStack(ItemTypes.BOW));
      }
      entities.add(e);
    }

    getRegion().getExtent().spawnEntities(entities, getSpawnCause());
  }

  @Override
  public void run() {
    if (isEmpty()) {
      return;
    }

    killPlayersInLava();
    runOreCheck();
    dropResults();
    summonMobs();
  }

  @Override
  public void forceEnd() {
    remove(getPlayers(PARTICIPANT));
  }

  private boolean isValidTeleportDestination(Location<World> targetPoint) {
    return targetPoint.getBlockType() == BlockTypes.AIR
        && targetPoint.add(0, -1, 0).getBlockType() == BlockTypes.STONEBRICK;
  }

  private Location<World> getRandomEntryPoint() {
    Vector3i minimumPoint = getRegion().getMinimumPoint();
    Vector3i maximumPoint = getRegion().getMaximumPoint();

    Location<World> targetPoint;
    do {
      double targetX = Probability.getRangedRandom(minimumPoint.getX(), maximumPoint.getX()) + .5;
      double targetY = minimumPoint.getY() + 7;
      double targetZ = Probability.getRangedRandom(minimumPoint.getZ(), maximumPoint.getZ()) + .5;

      targetPoint = new Location<>(getRegion().getExtent(), targetX, targetY, targetZ);
    } while (!isValidTeleportDestination(targetPoint));

    return targetPoint;
  }

  @Override
  public Clause<Player, ZoneStatus> add(Player player) {
    player.setLocation(getRandomEntryPoint());

    return new Clause<>(player, ZoneStatus.ADDED);
  }
}
