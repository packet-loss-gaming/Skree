/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityHealthPrinter {
    private Optional<Text> living;
    private Optional<Text> dead;

    public EntityHealthPrinter(Optional<Text> living, Optional<Text> dead) {
        this.living = living;
        this.dead = dead;
    }

    public void print(MessageSink sink, Living entity) {
        Double health = entity.get(Keys.HEALTH).get();
        if (health > 0) {
            printLiving(sink, entity);
        } else {
            printDead(sink, entity);
        }
    }

    private Map<String, Object> getFormatMap(Living living) {
        Double health = living.get(Keys.HEALTH).get();
        Double maxHealth = living.get(Keys.MAX_HEALTH).get();

        Map<String, Object> map = new HashMap<>();
        map.put("health", health);
        map.put("max health", maxHealth);
        map.put("health int", (int) Math.ceil(health));
        map.put("max health int", (int) Math.ceil(maxHealth));

        return map;
    }

    private Text format(Text formatStr, Living living) {
        return Texts.format(formatStr, getFormatMap(living));
    }

    private void printLiving(MessageSink sink, Living entity) {
        if (!living.isPresent()) {
            return;
        }

        sink.sendMessage(format(living.get(), entity));
    }

    private void printDead(MessageSink sink, Living entity) {
        if (!dead.isPresent()) {
            return;
        }

        sink.sendMessage(format(dead.get(), entity));
    }
}
