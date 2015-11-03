/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.playerstate;

import com.skelril.skree.service.PlayerStateService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.io.IOException;

public class PlayerStateServiceImpl implements PlayerStateService {

    private File getFile(Player player) {
        File file = new File("./mods/skree/profiles/");
        file.mkdirs();
        file = new File(file, player.getUniqueId() + ".dat");
        return file;
    }

    @Override
    public void save(Player player, String saveName) {
        NBTTagList playerInv = new NBTTagList();
        ((EntityPlayer) player).inventory.writeToNBT(playerInv);

        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player));
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            compound.setTag(saveName, playerInv);

            CompressedStreamTools.safeWrite(compound, getFile(player));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(Player player, String saveName) {
        try {
            NBTTagCompound compound = CompressedStreamTools.read(getFile(player));
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
