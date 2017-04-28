/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LoaderRegistry {
    private Map<Loader, Path> loaders = new HashMap<>();
    private Map<String, String> constants = new HashMap<>();

    public void registerLoader(Loader loader, Path path) {
        loaders.put(loader, path);
    }

    public void registerConstant(String constantName, String value) {
        constants.putIfAbsent(constantName, value);
    }

    private String replaceConstants(String fileContent) {
        for (Map.Entry<String, String> entry : constants.entrySet()) {
            String constant = entry.getKey();
            String value = entry.getValue();

            fileContent = fileContent.replace("%" + constant +  "%", value);
        }

        return fileContent;
    }

    public void loadAll() {
        loaders.forEach((loader, path) -> {
            try {
                Files.walk(path).forEach(subPath -> {
                    String filename = subPath.getFileName().toString();
                    if (filename.endsWith(".json")) {
                        try {
                            String fileContent = Joiner.on('\n').join(Files.readAllLines(subPath));
                            loader.load(replaceConstants(fileContent));
                        } catch (Exception e) {
                            System.err.println("Error loading: " + filename);
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
