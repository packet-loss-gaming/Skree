/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.*;
import java.util.Optional;

import static org.spongepowered.api.data.persistence.DataTranslators.CONFIGURATION_NODE;

public class ItemSerializer {
    public static Optional<JsonElement> serializeItemStack(ItemStack item) {
        try (StringWriter sink = new StringWriter()) {
            try (BufferedWriter writer = new BufferedWriter(sink)) {
                GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> writer).build();
                ConfigurationNode node = CONFIGURATION_NODE.translate(item.toContainer());
                loader.save(node);
                return Optional.of(new JsonParser().parse(sink.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ItemStack> deserializeItemStack(JsonElement element) {
        try (StringReader source = new StringReader(element.toString())) {
            try (BufferedReader reader = new BufferedReader(source)) {
                GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> reader).build();
                ConfigurationNode node = loader.load();
                return Optional.of(ItemStack.builder().fromContainer(CONFIGURATION_NODE.translate(node)).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
