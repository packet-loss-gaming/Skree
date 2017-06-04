/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.nitro.JarResourceLoader;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static Path getFile(String configName) throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve(configName);
    }

    public static <T> T loadConfig(String configName, Class<T> configClass) throws IOException {
        Path targetFile = getFile(configName);
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();

        if (!Files.exists(targetFile)) {
            new JarResourceLoader("/defaults/").loadFromResources((getResource) -> {
                try {
                    Files.copy(getResource.apply(configName), targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
            return gson.fromJson(reader, configClass);
        }
    }
}
