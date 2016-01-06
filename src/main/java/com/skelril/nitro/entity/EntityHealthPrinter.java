/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.skelril.nitro.text.CombinedText;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityHealthPrinter {
    private Optional<CombinedText> living;
    private Optional<CombinedText> dead;

    public EntityHealthPrinter(Optional<CombinedText> living, Optional<CombinedText> dead) {
        this.living = living;
        this.dead = dead;
    }

    public void print(MessageChannel sink, Living entity) {
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

    private Text format(CombinedText formatStr, Living living) {
        return formatStr.substitue(getFormatMap(living));
    }

    private void printLiving(MessageChannel channel, Living entity) {
        if (!living.isPresent()) {
            return;
        }

        channel.send(format(living.get(), entity));
    }

    private void printDead(MessageChannel channel, Living entity) {
        if (!dead.isPresent()) {
            return;
        }

        channel.send(format(dead.get(), entity));
    }
}
