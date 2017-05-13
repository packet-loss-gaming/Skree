/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import com.google.gson.*;
import com.skelril.nitro.registry.dynamic.item.ability.grouptype.MeleeAttackCluster;
import com.skelril.nitro.registry.dynamic.item.ability.grouptype.PointOfContactCluster;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AbilityGroupDeserializer implements JsonDeserializer<AbilityGroup> {
    private static final Map<String, Class<? extends AbilityCluster>> clusterMapping = new HashMap<>();

    static {
        clusterMapping.put("melee_attacks", MeleeAttackCluster.class);
        clusterMapping.put("point_of_contact", PointOfContactCluster.class);
    }

    @Override
    public AbilityGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        AbilityCooldownProfile profile = null;
        if (object.has("cool_down")) {
            profile = context.deserialize(object.get("cool_down"), AbilityCooldownProfile.class);
        }

        if (profile == null) {
            profile = new AbilityCooldownProfile();
        }

        AbilityGroup group = new AbilityGroup(profile);

        clusterMapping.forEach((key, clazz) -> {
            if (!object.has(key)) {
                return;
            }

            group.getClusters().add(context.deserialize(object, clazz));
        });

        return group;
    }
}
