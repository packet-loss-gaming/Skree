/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.build;

import com.google.common.collect.Lists;
import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HorseVariants;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Egg;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class BuildWorldWrapper extends WorldEffectWrapperImpl {
  private BuildConfig config;

  public BuildWorldWrapper(BuildConfig config) {
    this(config, new ArrayList<>());
  }

  public BuildWorldWrapper(BuildConfig config, Collection<World> worlds) {
    super("Build", worlds);
    this.config = config;
  }

  @Override
  public void addWorld(World world) {
    super.addWorld(world);
    tf(world).setAllowedSpawnTypes(false, true);
  }

  @Listener
  public void onEntityConstruction(ConstructEntityEvent.Pre event) {
    if (!isApplicable(event.getTransform().getExtent())) {
      return;
    }

    if (Monster.class.isAssignableFrom(event.getTargetType().getEntityClass())) {
      event.setCancelled(true);
    }
  }

  @Listener
  public void onEntitySpawn(SpawnEntityEvent event) {
    List<Entity> entities = event.getEntities();

    Optional<BlockSpawnCause> optBlockCause = event.getCause().first(BlockSpawnCause.class);
    for (Entity entity : entities) {
      if (!isApplicable(entity)) {
        continue;
      }

      if (entity instanceof Lightning) {
        ((Lightning) entity).setEffect(true);
        continue;
      }

      if (entity instanceof Egg && optBlockCause.isPresent()) {
        new ItemDropper(entity.getLocation()).dropStacks(
            Lists.newArrayList(newItemStack(ItemTypes.EGG)), SpawnTypes.DISPENSE
        );
        event.setCancelled(true);
        return;
      }

      if (entity instanceof Monster || (entity instanceof Horse && entity.get(Keys.HORSE_VARIANT).get().equals(HorseVariants.SKELETON_HORSE))) {
        event.setCancelled(true);
        return;
      }
    }
  }

  private PlayerCombatParser createFor(Cancellable event) {
    return new PlayerCombatParser() {
      @Override
      public void processPvP(Player attacker, Player defender) {
        Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
        if (optService.isPresent()) {
          PvPService service = optService.get();
          if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
            return;
          }
        }

        attacker.sendMessage(Text.of(TextColors.RED, "PvP is opt-in only in build worlds!"));

        event.setCancelled(true);
      }
    };
  }

  @Listener
  public void onPlayerCombat(DamageEntityEvent event) {
    if (!isApplicable(event.getTargetEntity())) {
      return;
    }

    createFor(event).parse(event);
  }

  @Listener
  public void onPlayerCombat(CollideEntityEvent.Impact event, @First Projectile projectile) {
    if (!isApplicable(projectile)) {
      return;
    }

    createFor(event).parse(event);
  }

  private List<Location<World>> markedOrePoints = new ArrayList<>();

  @Listener
  public void onBlockBreak(ChangeBlockEvent.Break event, @Named(NamedCause.SOURCE) Entity srcEnt) {
    if (!isApplicable(srcEnt)) {
      return;
    }

    List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
    for (Transaction<BlockSnapshot> block : transactions) {
      BlockSnapshot original = block.getOriginal();
      if (original.getCreator().isPresent()) {
        continue;
      }

      Optional<Location<World>> optLoc = original.getLocation();

      if (!optLoc.isPresent()) {
        continue;
      }

      Location<World> loc = optLoc.get();

      BlockState state = original.getState();

      // Prevent item dupe glitch by removing the position before subsequent breaks
      markedOrePoints.remove(loc);
      if (config.getDropModification().blocks(state)) {
        markedOrePoints.add(loc);
      }
    }
  }

  @Listener
  public void onItemDrop(DropItemEvent.Destruct event, @Named(NamedCause.SOURCE) BlockSpawnCause spawnCause) {
    BlockSnapshot blockSnapshot = spawnCause.getBlockSnapshot();

    Optional<Location<World>> optLocation = blockSnapshot.getLocation();
    if (!optLocation.isPresent()) {
      return;
    }

    Location<World> loc = optLocation.get();
    if (!markedOrePoints.remove(loc)) {
      return;
    }

    event.getEntities().clear();
  }
}