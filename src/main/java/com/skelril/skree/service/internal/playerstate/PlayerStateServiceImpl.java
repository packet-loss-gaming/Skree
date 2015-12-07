/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.PlayerStateService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlayerStateServiceImpl implements PlayerStateService {

    private Path getFile(Player player) throws IOException {
        ConfigManager service = SkreePlugin.inst().getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        path = Files.createDirectories(path.resolve("profiles"));
        return path.resolve(player.getUniqueId() + ".dat");
    }

    @Override
    public void save(Player player, String saveName) {
        NBTTagList playerInv = new NBTTagList();
        ((EntityPlayer) player).inventory.writeToNBT(playerInv);

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
                ((EntityPlayer) player).inventory.readFromNBT((NBTTagList) tag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
