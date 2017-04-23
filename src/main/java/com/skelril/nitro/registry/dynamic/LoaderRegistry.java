/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LoaderRegistry {
    private Map<Loader, Path> loaders = new HashMap<>();

    public void registerLoader(Loader loader, Path path) {
        loaders.put(loader, path);
    }

    public void loadAll() {
        loaders.forEach((loader, path) -> {
            try {
                Files.walk(path).forEach(subPath -> {
                    if (subPath.getFileName().toString().endsWith(".json")) {
                        try {
                            loader.load(subPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
