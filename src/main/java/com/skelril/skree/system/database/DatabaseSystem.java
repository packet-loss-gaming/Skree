/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.db.SQLHandle;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NModule(name = "Database System")
public class DatabaseSystem {

    private Path getDatabaseFile() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve("database.json");
    }

    @NModuleTrigger(trigger = "PRE_INITIALIZATION")
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Insert ugly configuration code
        try {
            Path targetFile = getDatabaseFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (Files.exists(targetFile)) {
                try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
                    DatabaseConfig config = gson.fromJson(reader, DatabaseConfig.class);

                    if (config == null) {
                        return;
                    }

                    SQLHandle.setDatabase(config.getDatabase());
                    SQLHandle.setUsername(config.getUsername());
                    SQLHandle.setPassword(config.getPassword());
                }
            } else {
                Files.createFile(targetFile);
                try (BufferedWriter writer = Files.newBufferedWriter(targetFile)) {
                    writer.write(gson.toJson(new DatabaseConfig()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
