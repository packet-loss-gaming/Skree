/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.templeoffate;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.nitro.droptable.DropTable;
import com.skelril.nitro.droptable.DropTableEntryImpl;
import com.skelril.nitro.droptable.DropTableImpl;
import com.skelril.nitro.droptable.MasterDropTable;
import com.skelril.nitro.droptable.resolver.SimpleDropResolver;
import com.skelril.nitro.droptable.roller.SlipperySingleHitDiceRoller;
import com.skelril.skree.content.zone.LegacyZoneBase;
import com.skelril.skree.service.PlayerStateService;
import com.skelril.skree.service.internal.playerstate.InventoryStorageStateException;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.skree.service.internal.zone.PlayerClassifier.PARTICIPANT;

public class TempleOfFateInstance extends LegacyZoneBase implements Runnable {

  private Location<World> startingPoint;
  private DropTable dropTable;

  private List<Player> participants = new ArrayList<>();

  public TempleOfFateInstance(ZoneRegion region) {
    super(region);
  }

  private void setUp() {
    startingPoint = new Location<>(getRegion().getExtent(), getRegion().getMinimumPoint().add(4, 11, 58));

    SlipperySingleHitDiceRoller slipRoller = new SlipperySingleHitDiceRoller();
    dropTable = new MasterDropTable(
        slipRoller,
        Lists.newArrayList(
            new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack("skree:ancient_metal_fragment")
                            )
                        ), 1
                    )
                )
            ),
            new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack("skree:emblem_of_hallow")
                            )
                        ), 30
                    )
                )
            ),
            new DropTableImpl(
                slipRoller,
                Lists.newArrayList(
                    new DropTableEntryImpl(
                        new SimpleDropResolver(
                            Lists.newArrayList(
                                newItemStack("skree:emblem_of_the_forge")
                            )
                        ), 30
                    )
                )
            )
        )
    );
  }

  public void rewardPlayer(Player player) {
    boolean participated = participants.contains(player);

    remove(player);
    if (!participated) {
      return;
    }

    for (ItemStack stack : dropTable.getDrops(1)) {
      player.getInventory().offer(stack);
    }
  }

  public void tryInventoryRestore(Player player) {
    Optional<PlayerStateService> optService = Sponge.getServiceManager().provide(PlayerStateService.class);
    if (optService.isPresent()) {
      PlayerStateService service = optService.get();
      service.loadInventoryIfStored(player);
    }
  }

  @Override
  public boolean init() {
    remove();
    setUp();
    return true;
  }

  private void outOfBoundsCheck() {
    getPlayers(PARTICIPANT).stream().filter(p -> !contains(p)).collect(Collectors.toList()).forEach(this::remove);
  }

  private void feedPlayers() {
    for (Player player : getPlayers(PARTICIPANT)) {
      player.offer(Keys.FOOD_LEVEL, 20);
      player.offer(Keys.SATURATION, 5D);
    }
  }

  @Override
  public void run() {
    outOfBoundsCheck();
    feedPlayers();
  }

  @Override
  public void forceEnd() {
    remove(getPlayers(PARTICIPANT));
  }

  @Override
  public Clause<Player, ZoneStatus> add(Player player) {
    player.setLocation(startingPoint);
    Optional<PlayerStateService> optService = Sponge.getServiceManager().provide(PlayerStateService.class);
    if (optService.isPresent()) {
      PlayerStateService service = optService.get();
      try {
        service.storeInventory(player);
        service.releaseInventory(player);

        player.offer(Keys.POTION_EFFECTS, new ArrayList<>());
        player.getInventory().clear();
      } catch (InventoryStorageStateException e) {
        e.printStackTrace();
        return new Clause<>(player, ZoneStatus.ERROR);
      }
    }

    participants.add(player);

    return new Clause<>(player, ZoneStatus.ADDED);
  }

  @Override
  public Clause<Player, ZoneStatus> remove(Player player) {
    player.getInventory().clear();
    tryInventoryRestore(player);
    participants.remove(player);

    return super.remove(player);
  }

  @Override
  public Collection<Player> getPlayers(PlayerClassifier classifier) {
    if (classifier == PARTICIPANT) {
      return participants;
    }
    return super.getPlayers(classifier);
  }
}
