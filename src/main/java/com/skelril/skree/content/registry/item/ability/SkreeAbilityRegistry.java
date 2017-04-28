/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability;

import com.skelril.nitro.registry.dynamic.item.ability.Ability;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityRegistry;
import com.skelril.skree.content.registry.item.ability.combat.*;

import java.util.HashMap;
import java.util.Map;

public class SkreeAbilityRegistry implements AbilityRegistry {
    private Map<String, Class<? extends Ability>> registry = new HashMap<>();

    public SkreeAbilityRegistry() {
        registerAbility("agility", Agility.class);
        registerAbility("doom_blade", DoomBlade.class);
        registerAbility("evil_focus", EvilFocus.class);
        registerAbility("healing_light", HealingLight.class);
        registerAbility("life_leech", LifeLeech.class);
        registerAbility("regen", Regen.class);
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
