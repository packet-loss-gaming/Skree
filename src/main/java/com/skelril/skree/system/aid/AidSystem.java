/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.aid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.nitro.JarResourceLoader;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.aid.AntiPeskListener;
import com.skelril.skree.content.aid.ChatCommandAid;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NModule(name = "Aid System")
public class AidSystem {
    private Path getWorldConfiguration() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve("anti_pesk.json");
    }

    private AntiPeskConfig loadAntiPeskConfig() {
        // Insert ugly configuration code
        try {
            Path targetFile = getWorldConfiguration();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(targetFile)) {
                new JarResourceLoader("/defaults/").loadFromResources((getResource) -> {
                    try {
                        Files.copy(getResource.apply("anti_pesk.json"), targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
                return gson.fromJson(reader, AntiPeskConfig.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ChatCommandAid());
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new AntiPeskListener(loadAntiPeskConfig()));
    }
}

