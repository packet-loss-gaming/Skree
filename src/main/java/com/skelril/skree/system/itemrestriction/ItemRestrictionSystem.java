/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.itemrestriction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.nitro.JarResourceLoader;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.itemrestriction.ItemInteractBlockingListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@NModule(name = "Item Restriction System")
public class ItemRestrictionSystem {
    private Path getItemRestrictionFile() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve("item_restriction.json");
    }

    private ItemRestrictionConfig loadConfiguration() {
        // Insert ugly configuration code
        try {
            Path targetFile = getItemRestrictionFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(targetFile)) {
                new JarResourceLoader("/defaults/").loadFromResources((getResource) -> {
                    try {
                        Files.copy(getResource.apply("item_restriction.json"), targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
                return gson.fromJson(reader, ItemRestrictionConfig.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        ItemRestrictionConfig itemRestrictionConfig = loadConfiguration();
        Set<String> blockedItems = itemRestrictionConfig.getBlockedItems();
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), new ItemInteractBlockingListener(blockedItems));
    }
}