/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.db.SQLHandle;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.config.ConfigService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class DatabaseSystem {

    private Path getDatabaseFile() throws IOException {
        Optional<ConfigService> optService = SkreePlugin.inst().getGame().getServiceManager().provide(ConfigService.class);
        if (optService.isPresent()) {
            ConfigService service = optService.get();
            Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
            return path.resolve("database.json");
        }
        throw new FileNotFoundException();
    }

    public DatabaseSystem(SkreePlugin plugin, Game game) {

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
