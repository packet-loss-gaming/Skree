/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Loader<ConfigType> {
    default void load(Path config) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (BufferedReader reader = Files.newBufferedReader(config)) {
            load(gson.fromJson(reader, getConfigClass()));
        }
    }

    void load(ConfigType configObject);
    Class<ConfigType> getConfigClass();
}
