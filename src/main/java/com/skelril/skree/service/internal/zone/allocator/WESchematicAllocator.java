/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector3i;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class WESchematicAllocator implements ZoneSpaceAllocator {

    private final Path baseDir;

    public WESchematicAllocator(Path baseDir) {
        this.baseDir = baseDir;
    }

    private String getFileName(String managerName) {
        return managerName.replace(" ", "-");
    }

    private Path getFile(String managerName) {
        return baseDir.resolve(getFileName(managerName) + ".schematic");
    }

    private ClipboardHolder getHolder(String managerName, WorldData worldData) throws IOException {
        try (InputStream bis = Files.newInputStream(getFile(managerName))) {
            ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);
            Clipboard clipboard = reader.read(worldData);
            return new ClipboardHolder(clipboard, worldData);
        }
    }

    private HashMap<String, HashRef> hashRefMap = new HashMap<>();

    protected ZoneRegion pasteAt(WorldResolver world, Vector3i origin, String managerName, Consumer<ZoneRegion> callback) {
        EditSession transaction = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world.getWorldEditWorld(), -1);
        transaction.enableQueue();

        hashRefMap.computeIfAbsent(managerName, (a) -> {
            HashRef ref = new HashRef();
            try {
                ref.holder = getHolder(managerName, world.getWorldEditWorld().getWorldData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ref;
        });

        HashRef ref = hashRefMap.get(managerName);
        if (ref == null) {
            callback.accept(null);
            return null;
        }

        ++ref.refCount;

        Clipboard clipboard = ref.holder.getClipboard();
        Region clipReg = clipboard.getRegion();
        clipboard.setOrigin(clipReg.getMinimumPoint());

        Operation operation = ref.holder
                .createPaste(transaction, transaction.getWorld().getWorldData())
                .to(new Vector(origin.getX(), origin.getY(), origin.getZ()))
                .build();

        Vector dimensions = clipboard.getDimensions();

        ZoneRegion region = new ZoneRegion(
                world.getSpongeWorld(),
                origin,
                new Vector3i(dimensions.getX(), dimensions.getY(), dimensions.getZ())
        );

        RunManager.runOperation(operation, () -> {
            RunManager.runOperation(transaction.commit(), () -> {
                callback.accept(region);
                if (--ref.refCount == 0) {
                    hashRefMap.remove(managerName);
                }
            });
        });

        return region;
    }

    private class HashRef {
        public ClipboardHolder holder;
        public int refCount = 0;
    }
}
