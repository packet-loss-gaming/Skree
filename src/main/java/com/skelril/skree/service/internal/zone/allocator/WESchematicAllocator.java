/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector3i;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class WESchematicAllocator implements ZoneSpaceAllocator {

    private final File baseDir;

    public WESchematicAllocator(File baseDir) {
        this.baseDir = baseDir;
    }

    public String getFileName(String managerName) {
        return managerName.replace(" ", "-");
    }

    public File getFile(String managerName) {
        return new File(baseDir, getFileName(managerName) + ".schematic");
    }

    public ClipboardHolder getHolder(String managerName, WorldData worldData) throws IOException {
        try (FileInputStream fis = new FileInputStream(getFile(managerName))) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);
                Clipboard clipboard = reader.read(worldData);
                return new ClipboardHolder(clipboard, worldData);
            }
        }
    }

    public ZoneRegion pasteAt(WorldResolver world, Vector3i origin, String managerName) {
        EditSession transaction = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world.getWorldEditWorld(), -1);

        Operation operation;
        try {
            ClipboardHolder holder = getHolder(managerName, world.getWorldEditWorld().getWorldData());
            Region clipReg = holder.getClipboard().getRegion();
            holder.getClipboard().setOrigin(clipReg.getMinimumPoint());
            operation = holder
                    .createPaste(transaction, transaction.getWorld().getWorldData())
                    .to(new Vector(origin.getX(), origin.getY(), origin.getZ()))
                    .build();

            Operations.completeLegacy(operation);

            Vector dimensions = holder.getClipboard().getDimensions();

            return new ZoneRegion(world.getSpongeWorld(), origin, new Vector3i(dimensions.getX(), dimensions.getY(), dimensions.getZ()));
        } catch (IOException | MaxChangedBlocksException e) {
            e.printStackTrace();
            transaction.undo(transaction);
        }
        return null;
    }
}
