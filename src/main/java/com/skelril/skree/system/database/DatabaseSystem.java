package com.skelril.skree.system.database;

import com.google.gson.Gson;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.db.SQLHandle;
import org.spongepowered.api.Game;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DatabaseSystem {
    public DatabaseSystem(SkreePlugin plugin, Game game) {

        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Insert ugly configuration code
        File targetDir = new File("./mods/skree/config/");
        targetDir.mkdirs();

        File targetFile = new File(targetDir + "/database.json");
        Gson gson = new Gson();

        try {
            DatabaseConfig config = gson.fromJson(new FileReader(targetFile), DatabaseConfig.class);

            SQLHandle.setDatabase(config.getDatabase());
            SQLHandle.setUsername(config.getUsername());
            SQLHandle.setPassword(config.getPassword());
        } catch (Exception ex) {
            try {
                targetFile.createNewFile();
                new FileWriter(targetFile).write(gson.toJson(new DatabaseConfig()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
