/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.jungleraid;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.ThrownPotion;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class JungleRaidEffectListener {
  private final JungleRaidManager manager;

  public JungleRaidEffectListener(JungleRaidManager manager) {
    this.manager = manager;
  }

  @Listener(order = Order.LATE)
  public void onPlayerInteract(InteractBlockEvent.Primary.MainHand event, @First Player player) {
    Optional<Location<World>> optBlockLoc = event.getTargetBlock().getLocation();
    if (!optBlockLoc.isPresent()) {
      return;
    }

    Location<World> blockLoc = optBlockLoc.get();

    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(blockLoc);
    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();
    if (inst.isFlagEnabled(JungleRaidFlag.TITAN_MODE) && player.getUniqueId().equals(inst.getFlagData().titan)) {
      if (blockLoc.getBlockType() == BlockTypes.BEDROCK) {
        return;
      }

      // TODO Convert to the Sponge API
      ((net.minecraft.world.World) blockLoc.getExtent()).destroyBlock(
          new BlockPos(blockLoc.getX(), blockLoc.getY(), blockLoc.getZ()),
          true
      );
    }
  }

  @Listener
  public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();
    if (inst.isFlagEnabled(JungleRaidFlag.NO_BLOCK_BREAK)) {
      player.sendMessage(Text.of(TextColors.RED, "You cannot break blocks by hand this game."));
      event.setCancelled(true);
    } else if (inst.isFlagEnabled(JungleRaidFlag.NO_MINING)) {
      List<BlockType> unbreakableBlocks = Lists.newArrayList(BlockTypes.STONE, BlockTypes.GRASS, BlockTypes.DIRT);
      for (Transaction<BlockSnapshot> block : event.getTransactions()) {
        if (unbreakableBlocks.contains(block.getOriginal().getState().getType())) {
          player.sendMessage(Text.of(TextColors.RED, "You cannot mine this game."));
          event.setCancelled(true);
          return;
        }
      }
    }
  }

  @Listener
  public void onBlockBurn(ChangeBlockEvent event) {
    if (event.getCause().root() instanceof Player) {
      return;
    }

    for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
      BlockType finalType = transaction.getFinal().getState().getType();
      if (finalType != BlockTypes.FIRE) {
        continue;
      }

      Optional<JungleRaidInstance> optInst = manager.getApplicableZone(transaction.getOriginal().getLocation().get());
      if (optInst.isPresent()) {
        JungleRaidInstance inst = optInst.get();
        if (inst.isFlagEnabled(JungleRaidFlag.NO_FIRE_SPREAD)) {
          event.setCancelled(true);
        }
        break;
      }
    }
  }

  @Listener
  public void onProjectileHit(CollideEvent.Impact event, @First Entity entity) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(entity);
    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();

    if (inst.getState() != JungleRaidState.IN_PROGRESS) {
      return;
    }

    int explosionSize = 2;

    if (entity.getType() == EntityTypes.TIPPED_ARROW) {
      if (inst.isFlagEnabled(JungleRaidFlag.TORMENT_ARROWS)) {
        ProjectileSource shooter = ((Arrow) entity).getShooter();

        CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(entity.getLocation().getPosition(), 16, 16, 16);
        for (Entity e : entity.getNearbyEntities(en -> predicate.test(en.getLocation().getPosition()))) {
          if (e.equals(shooter)) {
            continue;
          }
          if (e instanceof Living && shooter instanceof Living) {
            e.damage(1, IndirectEntityDamageSource.builder().type(
                DamageTypes.PROJECTILE
            ).entity(entity).proxySource((Living) shooter).build());

            if (Probability.getChance(5)) {
              EntityHealthUtil.heal((Living) shooter, 1);
            }
          }
        }
      }
      if (inst.isFlagEnabled(JungleRaidFlag.EXPLOSIVE_ARROWS)) {
        if (inst.isFlagEnabled(JungleRaidFlag.SUPER)) {
          explosionSize = 4;
        }
      } else {
        return;
      }
    }
    if (entity instanceof Snowball) {
      if (inst.isFlagEnabled(JungleRaidFlag.GRENADES)) {
        if (inst.isFlagEnabled(JungleRaidFlag.SUPER)) {
          explosionSize = 10;
        } else {
          explosionSize = 6;
        }
      } else {
        return;
      }
    }

    if (entity instanceof ThrownPotion) {
      return;
    }

    entity.getLocation().getExtent().triggerExplosion(
        Explosion.builder()
            .radius(explosionSize)
            .location(entity.getLocation())
            .shouldDamageEntities(true)
            .shouldBreakBlocks(true)
            .build(),
        Cause.source(SkreePlugin.container()).build()
    );
  }


  @Listener
  public void onPlayerInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();

    Optional<ItemStack> optStack = player.getItemInHand(HandTypes.MAIN_HAND);
    if (!optStack.isPresent()) {
      return;
    }

    ItemStack stack = optStack.get();
    if (stack.getItem() == ItemTypes.COMPASS) {
      event.setUseBlockResult(Tristate.FALSE);

      if (inst.getState() == JungleRaidState.IN_PROGRESS && inst.isFlagEnabled(JungleRaidFlag.ENHANCED_COMPASS)) {
        Set<Text> resultSet = new HashSet<>();
        for (Player aPlayer : inst.getPlayers(PlayerClassifier.PARTICIPANT)) {

          // Check validity
          if (player.equals(aPlayer)) {
            continue;
          }

          // Check team
          if (inst.isFriendlyFire(player, aPlayer)) {
            continue;
          }

          TextColor color = tf(player).canEntityBeSeen(tf(aPlayer)) ? TextColors.DARK_RED : TextColors.RED;

          resultSet.add(Text.of(color, aPlayer.getName(), " - ", player.getLocation().getPosition().distance(aPlayer.getLocation().getPosition())));
        }

        if (resultSet.isEmpty()) {
          player.sendMessage(Text.of(TextColors.RED, "No players found."));
        }

        player.sendMessage(Text.of(TextColors.YELLOW, "Player - Distance"));
        player.sendMessages(resultSet);
      } else if (inst.getState() == JungleRaidState.INITIALIZE) {
        player.setLocation(inst.getRandomLocation());
      }
    }
  }

  private PlayerCombatParser createFor(Cancellable event, JungleRaidInstance inst) {
    return new PlayerCombatParser() {
      @Override
      public void processPvP(Player attacker, Player defender, @Nullable Entity indirectSource) {
        final boolean isDamageEntityEvent = event instanceof DamageEntityEvent;

        // Do Death Touch before anything else
        if (inst.isFlagEnabled(JungleRaidFlag.DEATH_TOUCH) && isDamageEntityEvent) {
          ((DamageEntityEvent) event).setBaseDamage(Math.pow(defender.get(Keys.MAX_HEALTH).orElse(20D), 3));
          return;
        }

        Optional<JungleRaidClass> optClass = inst.getClass(attacker);
        if (optClass.isPresent()) {
          JungleRaidClass jrClass = optClass.get();
          if (jrClass == JungleRaidClass.SNIPER) {
            Optional<ItemStack> optHeld = attacker.getItemInHand(HandTypes.MAIN_HAND);
            boolean hasWoodenSword = optHeld.isPresent() && optHeld.get().getItem() == ItemTypes.WOODEN_SWORD;

            if (indirectSource != null || !hasWoodenSword) {
              double distSq = attacker.getLocation().getPosition().distanceSquared(
                  defender.getLocation().getPosition()
              );
              double targetDistSq = Math.pow(70, 2);
              double ratio = Math.min(distSq, targetDistSq) / targetDistSq;

              if (isDamageEntityEvent) {
                // Handle damage modification
                ((DamageEntityEvent) event).setBaseDamage(
                    ((DamageEntityEvent) event).getBaseDamage() * ratio
                );
              } else {
                // Disable the arrow fire in the Impact event
                if (ratio < .7 && indirectSource != null) {
                  indirectSource.offer(Keys.FIRE_TICKS, 0);
                }
              }
            }
          }
        }

        if (inst.isFlagEnabled(JungleRaidFlag.TITAN_MODE) && attacker.getUniqueId().equals(inst.getFlagData().titan) && isDamageEntityEvent) {
          ((DamageEntityEvent) event).setBaseDamage(((DamageEntityEvent) event).getBaseDamage() * 2);
        }
      }

      @Override
      public void processNonLivingAttack(DamageSource attacker, Player defender) {
        if (!(event instanceof DamageEntityEvent)) {
          return;
        }

        if (attacker.getType() == DamageTypes.FALL) {
          BlockType belowType = defender.getLocation().add(0, -1, 0).getBlockType();
          if (inst.isFlagEnabled(JungleRaidFlag.TRAMPOLINE)) {
            Vector3d oldVel = defender.getVelocity();
            Vector3d newVel = new Vector3d(oldVel.getX(), 0, oldVel.getZ());
            defender.setVelocity(new Vector3d(0, .1, 0).mul(((DamageEntityEvent) event).getBaseDamage()).add(newVel));
            event.setCancelled(true);
          } else if (belowType == BlockTypes.LEAVES || belowType == BlockTypes.LEAVES2) {
            if (Probability.getChance(2)) {
              Vector3d oldVel = defender.getVelocity();
              Vector3d newVel = new Vector3d(
                  oldVel.getX() > 0 ? -.5 : .5,
                  0,
                  oldVel.getZ() > 0 ? -.5 : .5
              );
              defender.setVelocity(new Vector3d(0, .1, 0).mul(((DamageEntityEvent) event).getBaseDamage()).add(newVel));
            }
            event.setCancelled(true);
          }
        } else if (attacker.getType() == DamageTypes.CUSTOM) {
          if (inst.isFlagEnabled(JungleRaidFlag.EXPLOSIVE_ARROWS) || inst.isFlagEnabled(JungleRaidFlag.GRENADES)) {
            ((DamageEntityEvent) event).setBaseDamage(Math.min(((DamageEntityEvent) event).getBaseDamage(), 2));
          }
        }
      }
    };
  }

  @Listener
  public void onPlayerCombat(DamageEntityEvent event) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(event.getTargetEntity());

    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();

    createFor(event, inst).parse(event);
  }

  @Listener
  public void onPlayerCombat(CollideEntityEvent.Impact event, @First Projectile projectile) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(projectile);
    if (!optInst.isPresent()) {
      return;
    }

    JungleRaidInstance inst = optInst.get();

    createFor(event, inst).parse(event);
  }


  private void handleLoss(JungleRaidInstance inst, Player player) {
    FlagEffectData data = inst.getFlagData();

    boolean isTitanEnabled = inst.isFlagEnabled(JungleRaidFlag.TITAN_MODE);
    boolean isTitan = player.getUniqueId().equals(data.titan);

    // Normal Jungle Raid fireworks and stuff
    Color killerColor = Color.WHITE;
    Color teamColor = inst.getTeamColor(player).orElse(Color.WHITE);
    Optional<Player> optKiller = inst.getLastAttacker(player);
    if (optKiller.isPresent()) {
      Player killer = optKiller.get();
      killerColor = inst.getTeamColor(killer).orElse(Color.WHITE);
      if (isTitanEnabled) {
        if (isTitan) {
          data.titan = killer.getUniqueId();

          ItemStack teamHood = newItemStack(ItemTypes.LEATHER_HELMET);
          teamHood.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, "Titan Hood"));
          teamHood.offer(Keys.COLOR, Color.BLACK);
          // playerEquipment.set(EquipmentTypes.HEADWEAR, teamHood);
          tf(killer).inventory.armorInventory[3] = tf(teamHood);
        } else if (killer.getUniqueId().equals(data.titan)) {
          killerColor = Color.BLACK;
        }
      }
    }

    if (isTitan && data.titan.equals(player.getUniqueId())) {
      data.titan = null;
    }

    if (!inst.isFlagEnabled(JungleRaidFlag.DEATH_ROCKETS)) {
      return;
    }

    Location<World> playerLoc = player.getLocation();

    Color finalKillerColor = killerColor;
    for (int i = 0; i < 12; i++) {
      Task.builder().delayTicks(i * 4).execute(() -> {
        Firework firework = (Firework) inst.getRegion().getExtent().createEntity(EntityTypes.FIREWORK, playerLoc.getPosition());
        FireworkEffect fireworkEffect = FireworkEffect.builder()
            .flicker(Probability.getChance(2))
            .trail(Probability.getChance(2))
            .color(teamColor)
            .fade(finalKillerColor)
            .shape(FireworkShapes.CREEPER)
            .build();
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(fireworkEffect));
        firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, Probability.getRangedRandom(2, 5));
        inst.getRegion().getExtent().spawnEntity(
            firework, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
        );
      }).submit(SkreePlugin.inst());
    }
  }

  @Listener
  public void onClientLeave(ClientConnectionEvent.Disconnect event) {
    Player player = event.getTargetEntity();
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      JungleRaidInstance inst = optInst.get();

      handleLoss(inst, player);
    }
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player) {
    Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      JungleRaidInstance inst = optInst.get();

      handleLoss(inst, player);
    }
  }
}
