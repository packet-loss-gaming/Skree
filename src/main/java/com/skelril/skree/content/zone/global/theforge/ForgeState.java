/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// TODO Cleanup this mess
class ForgeState {
    private Map<ItemStack, Integer> results;

    public ForgeState() {
        this(new HashMap<>());
    }

    public ForgeState(Map<ItemStack, Integer> results) {
        this.results = results;
    }

    public Map<ItemStack, Integer> getResults() {
        return results;
    }

    private static Path getPersistenceFile() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        path = Files.createDirectories(path.resolve("forge"));
        return path.resolve("resources.json");
    }

    private static Gson getGson() {
        return new GsonBuilder().create();
    }

    public static ForgeState load() {
        try (BufferedReader reader = Files.newBufferedReader(getPersistenceFile())) {
            return getGson().fromJson(reader, SerializableForgeState.class).toForgeState();
        } catch (IOException e) {
            return new ForgeState(new HashMap<>());
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(getPersistenceFile())) {
            writer.write(getGson().toJson(new SerializableForgeState(this)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
