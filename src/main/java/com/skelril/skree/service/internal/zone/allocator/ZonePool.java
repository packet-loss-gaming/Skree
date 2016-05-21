/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.*;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.ZoneBoundingBox;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ZonePool {

    private List<ZoneBoundingBox> globalBoxList = new ArrayList<>();
    private Map<String, List<ZoneBoundingBox>> cacheRegions = new HashMap<>();
    private Map<String, Deque<ZoneBoundingBox>> regionQueue = new HashMap<>();

    public Vector2i getLastMarkedPoint() throws IllegalStateException {
        if (globalBoxList.isEmpty()) {
            return new Vector2i(0, 0);
        }

        globalBoxList.sort((a, b) -> {
            Vector3i aMax = a.getMaximumPoint();
            Vector3i bMax = b.getMaximumPoint();
            if (aMax.getX() < bMax.getX() || aMax.getZ() < bMax.getZ()) {
                return -1;
            }
            return 1;
        });

        Vector3i lastMax = globalBoxList.get(globalBoxList.size() - 1).getMaximumPoint();
        return new Vector2i(lastMax.getX() + 1, lastMax.getZ() + 1);
    }

    private void noSaveClaimNew(String manager, ZoneBoundingBox boundingBox) {
        cacheRegions.putIfAbsent(manager, new ArrayList<>());
        cacheRegions.get(manager).add(boundingBox);

        globalBoxList.add(boundingBox);
    }

    public void claimNew(String manager, ZoneBoundingBox boundingBox) {
        noSaveClaimNew(manager, new ZoneBoundingBox(boundingBox));

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void freeToPool(String manager, ZoneBoundingBox boundingBox) {
        regionQueue.putIfAbsent(manager, new ArrayDeque<>());
        regionQueue.get(manager).push(boundingBox);
    }

    public Optional<ZoneBoundingBox> getIfAvailable(String manager) {
        Deque<ZoneBoundingBox> queue = regionQueue.get(manager);
        if (queue != null) {
            return Optional.ofNullable(queue.poll());
        }
        return Optional.empty();
    }

    public void load() throws IOException, IllegalStateException {
        globalBoxList.clear();
        cacheRegions.clear();
        regionQueue.clear();

        Gson gson = new GsonBuilder().create();
        JsonParser parser = new JsonParser();

        try (BufferedReader reader = Files.newBufferedReader(getCacheFile())) {
            JsonObject object = parser.parse(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String manager = entry.getKey();

                JsonArray elements = entry.getValue().getAsJsonArray();
                for (JsonElement element : elements) {
                    ZoneBoundingBox boundingBox = gson.fromJson(element, ZoneBoundingBox.class);
                    noSaveClaimNew(manager, boundingBox);
                    freeToPool(manager, boundingBox);
                }
            }
        }
    }

    public void save() throws IOException {
        Gson gson = new GsonBuilder().create();

        try (BufferedWriter writer = Files.newBufferedWriter(getCacheFile())) {
            writer.write(gson.toJson(cacheRegions));
        }
    }

    private Path getCacheFile() throws IOException {
        Path cacheFile = getBaseWorkingDir().resolve("cache.json");
        if (!Files.exists(cacheFile)) {
            Files.createFile(cacheFile);
        }
        return cacheFile;
    }

    private static Path getBaseWorkingDir() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return Files.createDirectories(path.resolve("zones"));
    }
}
