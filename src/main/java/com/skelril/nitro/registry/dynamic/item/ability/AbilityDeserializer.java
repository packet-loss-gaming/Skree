/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AbilityDeserializer implements JsonDeserializer<Ability> {
    private AbilityRegistry registry;

    public AbilityDeserializer(AbilityRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Class<? extends Ability> abilityClass = registry.lookupByID(json.getAsJsonObject().get("id").getAsString());
        return context.deserialize(json, abilityClass);
    }
}
