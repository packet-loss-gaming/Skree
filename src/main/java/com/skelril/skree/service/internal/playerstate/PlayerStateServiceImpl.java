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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.config.ConfigService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PlayerStateServiceImpl implements PlayerStateService {

    private Path getFile(Player player) throws IOException {
        Optional<ConfigService> optService = SkreePlugin.inst().getGame().getServiceManager().provide(ConfigService.class);
        if (optService.isPresent()) {
            ConfigService service = optService.get();
            Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
            path = Files.createDirectories(path.resolve("profiles"));
            return path.resolve(player.getUniqueId() + ".dat");
        }
        throw new FileNotFoundException();
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
