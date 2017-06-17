/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageFunction;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class TheForgeListener {
  private final TheForgeManager manager;

  public TheForgeListener(TheForgeManager manager) {
    this.manager = manager;
  }

  private PlayerCombatParser createCombatParser(Event event, TheForgeInstance inst) {
    return new PlayerCombatParser() {
      @Override
      public void processPlayerAttack(Player attacker, Living defender) {
        if (!(event instanceof DamageEntityEvent)) {
          return;
        }

        if (attacker.getLocation().getY() != attacker.getLocation().getBlockY()) {
          DamageEntityEvent dEvent = (DamageEntityEvent) event;
          dEvent.setBaseDamage(Probability.getCompoundRandom(dEvent.getBaseDamage(), 4));
        }
      }

      @Override
      public void processMonsterAttack(Living attacker, Player defender) {
        if (!(event instanceof DamageEntityEvent)) {
          return;
        }

        if (inst.isInvunerable(defender)) {
          ((DamageEntityEvent) event).setCancelled(true);
          return;
        }

        if (Probability.getChance(5)) {
          DamageEntityEvent dEvent = (DamageEntityEvent) event;
          for (DamageFunction modifier : dEvent.getModifiers()) {
            dEvent.setDamage(modifier.getModifier(), (a) -> 0D);
          }
        }
      }
    };
  }

  @Listener
  public void onPlayerCombat(DamageEntityEvent event) {
    Optional<TheForgeInstance> optInst = manager.getApplicableZone(event.getTargetEntity());
    if (!optInst.isPresent()) {
      return;
    }

    createCombatParser(event, optInst.get()).parse(event);
  }

  @Listener
  public void onPlayerCombat(CollideEntityEvent.Impact event, @First Projectile projectile) {
    Optional<TheForgeInstance> optInst = manager.getApplicableZone(projectile);
    if (!optInst.isPresent()) {
      return;
    }

    createCombatParser(event, optInst.get()).parse(event);
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player) {
    Optional<TheForgeInstance> optInst = manager.getApplicableZone(player);
    if (optInst.isPresent()) {
      if (event.getMessage().toPlain().contains("died")) {
        event.setMessage(Text.of(player.getName(), " was incinerated at The Forge"));
      }
    }
  }
}
