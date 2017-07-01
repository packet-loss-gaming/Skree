/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability;

import com.skelril.nitro.registry.dynamic.ability.Ability;
import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.skree.content.registry.ability.combat.defensive.PowerBurst;
import com.skelril.skree.content.registry.ability.combat.offensive.*;
import com.skelril.skree.content.registry.ability.misc.CreatureImpact;
import com.skelril.skree.content.registry.ability.misc.ItemVortex;

import java.util.HashMap;
import java.util.Map;

public class SkreeAbilityRegistry implements AbilityRegistry {
  private Map<String, Class<? extends Ability>> registry = new HashMap<>();

  public SkreeAbilityRegistry() {
    registerOffensiveAbilities();
    registerDefensiveAbilities();
    registerMiscAbilities();
  }

  private void registerOffensiveAbilities() {
    registerAbility("agility", Agility.class);
    registerAbility("coin_toss", CoinToss.class);
    registerAbility("confuse", Confuse.class);
    registerAbility("curse", Curse.class);
    registerAbility("decimate", Decimate.class);
    registerAbility("doom_blade", DoomBlade.class);
    registerAbility("evil_focus", EvilFocus.class);
    registerAbility("famine", Famine.class);
    registerAbility("fear_blaze", FearBlaze.class);
    registerAbility("fear_bomb", FearBomb.class);
    registerAbility("fear_strike", FearStrike.class);
    registerAbility("glowing_fog", GlowingFog.class);
    registerAbility("healing_light", HealingLight.class);
    registerAbility("lightning_strike", LightningStrike.class);
    registerAbility("magic_chain", MagicChain.class);
    registerAbility("life_leech", LifeLeech.class);
    registerAbility("regen", Regen.class);
    registerAbility("soul_smite", SoulSmite.class);
    registerAbility("weaken", Weaken.class);
  }

  private void registerDefensiveAbilities() {
    registerAbility("power_burst", PowerBurst.class);
  }

  private void registerMiscAbilities() {
    registerAbility("creature_impact", CreatureImpact.class);
    registerAbility("item_vortex", ItemVortex.class);
  }

  @Override
  public Class<? extends Ability> lookupByID(String id) {
    return registry.get(id);
  }

  @Override
  public void registerAbility(String id, Class<? extends Ability> abilityClass) {
    registry.put(id, abilityClass);
  }
}
