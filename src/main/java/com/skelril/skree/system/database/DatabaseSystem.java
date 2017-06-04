/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.database;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.db.SQLHandle;
import com.skelril.skree.service.DatabaseService;
import com.skelril.skree.service.internal.database.DatabaseServiceImpl;
import com.skelril.skree.system.ConfigLoader;
import com.skelril.skree.system.ServiceProvider;
import org.flywaydb.core.Flyway;
import org.spongepowered.api.Sponge;

import java.io.IOException;

@NModule(name = "Database System")
public class DatabaseSystem implements ServiceProvider<DatabaseService> {
    private DatabaseService service;

    private void setupHandle(String database, String username, String password) {
        SQLHandle.setDatabase(database);
        SQLHandle.setUsername(username);
        SQLHandle.setPassword(password);
    }

    private void runMigrations(String database, String username, String password) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(database, username, password);
        flyway.setSchemas("mc_db");
        flyway.migrate();
    }

    @NModuleTrigger(trigger = "PRE_INITIALIZATION")
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            DatabaseConfig config = ConfigLoader.loadConfig("database.json", DatabaseConfig.class);

            String database = config.getDatabase();
            String username = config.getUsername();
            String password = config.getPassword();

            setupHandle(database, username, password);
            runMigrations(database, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        service = new DatabaseServiceImpl();
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), DatabaseService.class, service);
    }

    @Override
    public DatabaseService getService() {
        return service;
    }
}
