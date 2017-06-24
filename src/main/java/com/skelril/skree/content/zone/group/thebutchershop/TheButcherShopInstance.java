/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.thebutchershop;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.droptable.CofferResolver;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.HighScoreService;
import com.skelril.skree.service.internal.highscore.ScoreTypes;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.SPECTATOR;

public class TheButcherShopInstance extends LegacyZoneBase implements Runnable {
  private int ticks = 0;
  private int wave = 0;
  private int processedItems = 0;

  private DropTable dropTable;

  public TheButcherShopInstance(ZoneRegion region) {
    super(region);
  }

  @Override
  public boolean init() {
    setUp();
    remove();
    return true;
  }

  @Override
  public void forceEnd() {
    for (Player player : getPlayers(SPECTATOR)) {
      player.sendTitle(
          Title.builder()
              .title(Text.of(TextColors.RED, "Game Over"))
              .fadeIn(20)
              .fadeOut(20)
              .build()
      );
    }

    remove(getPlayers(PARTICIPANT));
    remove();
  }

  @Override
  public void remove() {
    remove(Monster.class, Animal.class, ExperienceOrb.class, Item.class, Arrow.class);
  }

  private boolean isValidTeleportDestination(Location<World> targetPoint) {
    return targetPoint.getBlockType() == BlockTypes.AIR
        && targetPoint.add(0, -1, 0).getBlockType() == BlockTypes.GRASS;
  }

  private Location<World> getRandomEntryPoint() {
    Vector3i minimumPoint = getRegion().getMinimumPoint();
    Vector3i maximumPoint = getRegion().getMaximumPoint();

    Location<World> targetPoint;
    do {
      double targetX = Probability.getRangedRandom(minimumPoint.getX(), maximumPoint.getX()) + .5;
      double targetY = minimumPoint.getY() + 4;
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

  @Override
  public Clause<Player, ZoneStatus> remove(Player player) {
    dropTable.getDrops(wave * 3).forEach((drop) -> player.getInventory().offer(drop));
    return super.remove(player);
  }

  private List<Location<World>> spawnPoints = new ArrayList<>();

  private void setUpDropTable() {
    SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller();
    dropTable = new MasterDropTable(
        slipRoller,
        Lists.newArrayList(
            new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                    new DropTableEntryImpl(new CofferResolver(15), 2)
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
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack(ItemTypes.COOKED_CHICKEN)
                            )
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack(ItemTypes.COOKED_FISH)
                            )
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack(ItemTypes.COOKED_MUTTON)
                            )
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack(ItemTypes.COOKED_PORKCHOP)
                            )
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack(ItemTypes.COOKED_RABBIT)
                            )
                        ), 3
                    ),
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack("skree:cooked_god_fish")
                            )
                        ), 15
                    )
                )
            )
        )
    );
  }

  private void setUpSpawnPoints() {
    Vector3i min = getRegion().getMinimumPoint();

    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(32, 10, 5)));
    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(29, 10, 3)));

    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(5, 10, 3)));
    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(3, 10, 5)));

    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(2, 10, 29)));
    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(5, 10, 31)));

    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(29, 10, 32)));
    spawnPoints.add(new Location<>(getRegion().getExtent(), min.add(31, 10, 29)));
  }

  private void runStartingWave() {
    Task.builder().execute(this::spawnWave).delay(5, TimeUnit.SECONDS).submit(SkreePlugin.inst());
  }

  private void setUp() {
    setUpDropTable();
    setUpSpawnPoints();

    runStartingWave();
  }

  private static final int TICKS_PER_WAVE = 60;

  @Override
  public void run() {
    if (isEmpty()) {
      expire();
      return;
    }

    if (!hasGameStarted()) {
      return;
    }

    shakeHidingAnimals();
    processItems();

    ++ticks;

    if (getTicksRemaining() <= 5 && getTicksRemaining() != 0) {
      printTicksRemaining();
    }

    if (allMobsProcessed()) {
      spawnWave();
    } else if (getTicksRemaining() <= 0) {
      expire();
    }
  }

  private boolean hasGameStarted() {
    return wave > 0;
  }

  public void shakeHidingAnimals() {
    getContained(Animal.class).stream().filter((animal) -> {
      int relativeYPosition = animal.getLocation().getPosition().getFloorY() - getRegion().getMinimumPoint().getY();
      return relativeYPosition > 9;
    }).forEach((animal) -> {
      animal.setVelocity(new Vector3d(
          Probability.getRangedRandom(-1, 1),
          0,
          Probability.getRangedRandom(-1, 1)
      ));
    });
  }

  public void processItems() {
    getContained(Item.class).forEach((item) -> {
      if (item.getLocation().add(0, -1, 0).getBlockType() != BlockTypes.GOLD_BLOCK) {
        return;
      }

      if (!item.getItemType().getId().equals("skree:packaged_meat")) {
        return;
      }

      processedItems += item.item().get().getCount();

      item.remove();
    });
  }

  public int getTicksRemaining() {
    return TICKS_PER_WAVE - ticks;
  }

  public void printTicksRemaining() {
    for (Player player : getPlayers(SPECTATOR)) {
      player.sendTitle(
          Title.builder()
              .title(Text.of(TextColors.RED, getTicksRemaining()))
              .fadeOut(5)
              .build()
      );
    }
  }

  public boolean allMobsProcessed() {
    return processedItems >= getMobsForWave();
  }

  private void spawnAnimal() {
    Location<World> spawnPoint = Probability.pickOneOf(spawnPoints).add(.5, 0, .5);

    Entity entity = spawnPoint.createEntity(EntityTypes.COW);
    spawnPoint.spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
  }

  public int getMobsForWave() {
    return (int) (getPlayers(PARTICIPANT).size() * wave * 1.25) + 5;
  }

  public void spawnWave() {
    ticks = 0;
    processedItems = 0;

    ++wave;

    for (int i = 0; i < getMobsForWave(); ++i) {
      spawnAnimal();
    }

    for (Player player : getPlayers(SPECTATOR)) {
      player.sendTitle(
          Title.builder()
              .title(Text.of(TextColors.RED, "Wave"))
              .subtitle(Text.of(TextColors.RED, wave))
              .fadeIn(20)
              .fadeOut(20)
              .build()
      );
    }

    Optional<HighScoreService> optHighScores = Sponge.getServiceManager().provide(HighScoreService.class);
    if (optHighScores.isPresent()) {
      HighScoreService highScores = optHighScores.get();

      for (Player player : getPlayers(PARTICIPANT)) {
        highScores.update(player, ScoreTypes.HIGHEST_BUTCHER_SHOP_WAVE, wave);
      }
    }
  }
}