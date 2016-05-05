/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.PlayerStateService;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class PlayerStateServiceImpl implements PlayerStateService {

    private static final String GENERAL_STORE_NAME = "general_store";
    private static final String RELEASE_STATE_STRING = "_release_state";

    private Path getFile(Player player) throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        path = Files.createDirectories(path.resolve("profiles"));
        return path.resolve(player.getUniqueId() + ".dat");
    }

    @Override
    public boolean hasInventoryStored(Player player) {
        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                return false;
            }

            NBTBase tag = compound.getTag(GENERAL_STORE_NAME);
            if (tag instanceof NBTTagList) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasReleasedInventoryStored(Player player) {
        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                return false;
            }

            NBTBase tag = compound.getTag(GENERAL_STORE_NAME);
            if (tag instanceof NBTTagList) {
                return compound.getBoolean(GENERAL_STORE_NAME + RELEASE_STATE_STRING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void storeInventory(Player player) throws InventoryStorageStateException {
        if (hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        save(player, GENERAL_STORE_NAME);
    }

    @Override
    public void loadInventory(Player player) throws InventoryStorageStateException {
        if (!hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        load(player, GENERAL_STORE_NAME);
        destroySave(player, GENERAL_STORE_NAME);
    }

    @Override
    public void releaseInventory(Player player) throws InventoryStorageStateException {
        if (!hasInventoryStored(player)) {
            throw new InventoryStorageStateException();
        }

        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            compound.setBoolean(GENERAL_STORE_NAME + RELEASE_STATE_STRING, true);

            CompressedStreamTools.safeWrite(compound, getFile(player).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Player player, String saveName) {
        NBTTagList playerInv = new NBTTagList();
        tf(player).inventory.writeToNBT(playerInv);

        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            compound.setTag(saveName, playerInv);

            CompressedStreamTools.safeWrite(compound, getFile(player).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(Player player, String saveName) {
        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                return;
            }

            NBTBase tag = compound.getTag(saveName);
            if (tag instanceof NBTTagList) {
                tf(player).inventory.readFromNBT((NBTTagList) tag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroySave(Player player, String saveName) {
        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player).toFile());
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            compound.setTag(saveName, null);
            if (saveName.equals(GENERAL_STORE_NAME)) {
                compound.setBoolean(saveName + RELEASE_STATE_STRING, false);
            }

            CompressedStreamTools.safeWrite(compound, getFile(player).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
        if (hasReleasedInventoryStored(player)) {
            try {
                loadInventory(player);
            } catch (InventoryStorageStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (hasReleasedInventoryStored(player)) {
            try {
                loadInventory(player);
            } catch (InventoryStorageStateException e) {
                e.printStackTrace();
            }
        }
    }
}
