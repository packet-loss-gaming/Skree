/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import com.google.gson.GsonBuilder;

public class AbilityEnabledGsonBuilder {
  public static GsonBuilder getGsonBuilder(GsonBuilder parentBuilder, AbilityRegistry abilityRegistry) {
    AbilityDeserializer abilityDeserializer = new AbilityDeserializer(abilityRegistry);

    return parentBuilder
        .registerTypeAdapter(Ability.class, abilityDeserializer)
        .registerTypeAdapter(SpecialAttack.class, abilityDeserializer)
        .registerTypeAdapter(PointOfContact.class, abilityDeserializer)
        .registerTypeAdapter(AbilityGroup.class, new AbilityGroupDeserializer());
  }
}
