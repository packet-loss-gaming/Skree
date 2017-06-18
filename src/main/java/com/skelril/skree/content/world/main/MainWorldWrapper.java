/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.main;


import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.position.PositionRandomizer;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.zone.ZoneWaitingLobby;
import com.skelril.skree.service.PvPService;
import com.skelril.skree.service.internal.world.WorldEffectWrapperImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.SkeletonHorse;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class MainWorldWrapper extends WorldEffectWrapperImpl implements Runnable {

  private ZoneWaitingLobby lobby = new ZoneWaitingLobby(() -> {
    Vector3i randomizedPos = new PositionRandomizer(5, 0, 5).createPosition3i(new Vector3i(122, 94, 103));

    return getWorlds().iterator().next().getLocation(randomizedPos);
  });

  public MainWorldWrapper() {
    this(new ArrayList<>());
  }

  public MainWorldWrapper(Collection<World> worlds) {
    super("Main", worlds);
    Sponge.getEventManager().registerListeners(SkreePlugin.inst(), lobby);

    Task.builder().execute(this).interval(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
  }

  public ZoneWaitingLobby getLobby() {
    return lobby;
  }

  @Override
  public void addWorld(World world) {
    super.addWorld(world);
    tf(world).setAllowedSpawnTypes(false, false);
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

    for (Entity entity : entities) {
      if (!isApplicable(entity)) {
        continue;
      }

      if (entity instanceof Lightning) {
        ((Lightning) entity).setEffect(true);
        continue;
      }

      if (entity instanceof Monster || (entity instanceof SkeletonHorse)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  private boolean check(Player player, Location<World> loc) {
    return !player.hasPermission("skree.admin.edit.main");
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent event, @First Player player) {
    for (Transaction<BlockSnapshot> block : event.getTransactions()) {
      Optional<Location<World>> optLoc = block.getOriginal().getLocation();
      if (optLoc.isPresent() && isApplicable(optLoc.get())) {
        boolean preventedFromBuilding = check(player, optLoc.get());

        // Block players that are allowed to build, otherwise send the no build message
        Text noEditMessage = Text.of(TextColors.RED, "You can't change blocks here!");
        if (!preventedFromBuilding) {
          if (player.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL) != GameModes.CREATIVE) {
            preventedFromBuilding = true;
            noEditMessage = Text.of(TextColors.RED, "You must be in creative mode to edit!");
          }
        }

        if (preventedFromBuilding) {
          if (event.getCause().root().equals(player)) {
            player.sendMessage(noEditMessage);
          }

          event.setCancelled(true);
          return;
        }
      }
    }
  }

  private PlayerCombatParser createFor(Cancellable event) {
    return new PlayerCombatParser() {
      @Override
      public void processPvP(Player attacker, Player defender, @Nullable Entity indirectSource) {
        Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
        if (optService.isPresent()) {
          PvPService service = optService.get();
          if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
            return;
          }
        }

        if (!(indirectSource instanceof Snowball) || !lobby.contains(attacker)) {
          attacker.sendMessage(Text.of(TextColors.RED, "PvP is opt-in only in the main world!"));
          event.setCancelled(true);
        }
      }

      @Override
      public void processNonLivingAttack(DamageSource attacker, Player defender) {
        if (attacker.getType() == DamageTypes.VOID) {
          defender.setLocation(defender.getWorld().getSpawnLocation());
          defender.offer(Keys.FALL_DISTANCE, 0F);
          event.setCancelled(true);
        }
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

  @Override
  public void run() {
    PotionEffect speedEffect = PotionEffect.builder()
        .duration(3 * 20)
        .amplifier(5)
        .particles(false)
        .potionType(PotionEffectTypes.SPEED)
        .build();

    for (World world : getWorlds()) {
      for (Entity entity : world.getEntities(p -> p.getType().equals(EntityTypes.PLAYER))) {
        if (entity.get(Keys.GAME_MODE).orElse(GameModes.CREATIVE) != GameModes.SURVIVAL) {
          continue;
        }

        List<PotionEffect> potionEffects = entity.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
        potionEffects.add(speedEffect);
        entity.offer(Keys.POTION_EFFECTS, potionEffects);
      }
    }
  }
}
