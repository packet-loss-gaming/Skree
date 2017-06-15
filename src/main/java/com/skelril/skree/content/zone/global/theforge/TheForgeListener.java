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
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.Optional;
import java.util.function.Function;

public class TheForgeListener {
  private final TheForgeManager manager;

  public TheForgeListener(TheForgeManager manager) {
    this.manager = manager;
  }

  @Listener
  public void onPlayerCombat(DamageEntityEvent event) {
    Optional<TheForgeInstance> optInst = manager.getApplicableZone(event.getTargetEntity());
    if (!optInst.isPresent()) {
      return;
    }

    new PlayerCombatParser() {
      @Override
      public void processPlayerAttack(Player attacker, Living defender) {
        if (attacker.getLocation().getY() != attacker.getLocation().getBlockY()) {
          event.setBaseDamage(Probability.getCompoundRandom(event.getBaseDamage(), 4));
        }
      }
    }.parse(event);
  }

  @Listener
  public void onPlayerCombat(CollideEntityEvent.Impact event, @First Projectile projectile) {
    Optional<TheForgeInstance> optInst = manager.getApplicableZone(projectile);
    if (!optInst.isPresent()) {
      return;
    }

    new PlayerCombatParser() {
      @Override
      public void processMonsterAttack(Living attacker, Player defender) {
        if (!(event instanceof DamageEntityEvent)) {
          return;
        }

        DamageEntityEvent dEvent = (DamageEntityEvent) event;
        for (Tuple<DamageModifier, Function<? super Double, Double>> modifier : dEvent.getModifiers()) {
          dEvent.setDamage(modifier.getFirst(), (a) -> Math.floor(a * .5));
        }
      }
    }.parse(event);
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
