/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import com.google.gson.*;
import com.skelril.nitro.registry.dynamic.item.ability.grouptype.MeleeAttackGroup;

import java.lang.reflect.Type;

public class AbilityGroupDeserializer implements JsonDeserializer<AbilityGroup> {
    @Override
    public AbilityGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        if (object.has("melee_attacks")) {
            return context.deserialize(json, MeleeAttackGroup.class);
        }

        return null;
    }
}
