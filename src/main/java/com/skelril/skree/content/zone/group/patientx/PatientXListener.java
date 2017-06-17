/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.patientx;

import com.google.common.collect.Lists;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.world.ExplosionEvent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageFunction;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class PatientXListener {

  private final PatientXManager manager;

  public PatientXListener(PatientXManager manager) {
    this.manager = manager;
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent event) {
    // Depends on SpongeForge#550
    if (event instanceof ExplosionEvent.Detonate) {
      return;
    }

    Optional<PluginContainer> optPluginContainer = event.getCause().first(PluginContainer.class);
    if (optPluginContainer.isPresent()) {
      return;
    }

    for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
      if (manager.getApplicableZone(transaction.getOriginal().getLocation().get()).isPresent()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @Listener(order = Order.LAST)
  public void onEntityDamageEvent(DamageEntityEvent event) {
    Entity defender = event.getTargetEntity();

    Optional<PatientXInstance> optInst = manager.getApplicableZone(defender);
    if (!optInst.isPresent()) {
      return;
    }

    PatientXInstance inst = optInst.get();

    DamageSource dmgSource = event.getCause().first(DamageSource.class).get();

    if (defender instanceof Player && manager.getBlockedDamage().contains(dmgSource.getType())) {
      // Explosive damage formula: (1 × 1 + 1) × 8 × power + 1
      // Use 49, snowball power is 3
      double ratio = event.getBaseDamage() / 49;

      // Nullify all modifiers
      for (DamageFunction modifier : event.getModifiers()) {
        event.setDamage(modifier.getModifier(), (a) -> 0D);
      }

      event.setBaseDamage(ratio * inst.getDifficulty());
    }
  }

  @Listener
  public void onProjectileHit(CollideEvent.Impact event, @First Entity entity) {
    Optional<PatientXInstance> optInst = manager.getApplicableZone(entity);
    if (!optInst.isPresent()) {
      return;
    }

    if (entity instanceof Snowball) {
      if (!event.getCause().containsType(Player.class)) {
        entity.getLocation().getExtent().triggerExplosion(
            Explosion.builder()
                .radius(3)
                .location(entity.getLocation())
                .shouldDamageEntities(true)
                .build(),
            Cause.source(SkreePlugin.container()).build()
        );
      }
    }
  }

  @Listener
  public void onEntityDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Zombie zombie) {
    Optional<PatientXInstance> optInst = manager.getApplicableZone(zombie);
    if (!optInst.isPresent()) {
      return;
    }

    if (((EntityZombie) zombie).isChild()) {
      if (Probability.getChance(10)) {
        Task.builder().execute(() -> {
          new ItemDropper(zombie.getLocation()).dropStacks(
              Lists.newArrayList(newItemStack(ItemTypes.GOLD_INGOT, Probability.getRandom(16))),
              SpawnTypes.DROPPED_ITEM
          );
        }).delayTicks(1).submit(SkreePlugin.inst());
      }
    }
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player) {
    Optional<PatientXInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      PatientXInstance inst = optInst.get();
      inst.healBoss(.25F);
      inst.resetDifficulty();

      inst.sendAttackBroadcast(
          "Haha, bow down " + player.getName() + ", show's over for you.",
          PatientXInstance.AttackSeverity.INFO
      );

      Optional<PatientXAttack> optAttack = inst.getLastAttack();
      String deathMessage = " froze";
      if (optAttack.isPresent()) {
        switch (optAttack.get()) {
          case MUSICAL_CHAIRS:
            deathMessage = " tripped over a chair";
            break;
          case SMASHING_HIT:
            deathMessage = " got smashed";
            break;
          case BOMB_PERFORMANCE:
            deathMessage = " bombed a performance evaluation";
            break;
          case WITHER_AWAY:
            deathMessage = " became a fellow candle";
            break;
          case SPLASH_TO_IT:
            deathMessage = " loves toxic fluids";
            break;
          case COLD_FEET:
            deathMessage = " lost a foot or two";
            break;
          case IM_JUST_BATTY:
            deathMessage = " went batty";
            break;
          case RADIATION:
            deathMessage = " was irradiated";
            break;
          case SNOWBALL_FIGHT:
            deathMessage = " took a snowball to the face";
            break;
        }
      }

      event.setMessage(Text.of(player.getName(), deathMessage));
    }
  }
}
