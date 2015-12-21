/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.command.sponge;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class PlayerProvider implements Provider<Player> {
    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public Player get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next().toLowerCase();
        List<String> similar = new ArrayList<>();
        for (Player player : SkreePlugin.inst().getGame().getServer().getOnlinePlayers()) {
            String playerName = player.getName().toLowerCase();
            if (playerName.equals(name)) {
                return player;
            }
            if (playerName.startsWith(name)) {
                similar.add(player.getName());
            }
        }

        if (similar.isEmpty()) {
            throw new ArgumentParseException("No players matched.");
        }
        throw new ArgumentParseException("No players matched, did you mean " + Joiner.on(", ").join(similar) + '?');
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        List<String> similar = new ArrayList<>();
        for (Player player : SkreePlugin.inst().getGame().getServer().getOnlinePlayers()) {
            String playerName = player.getName();
            if (playerName.startsWith(prefix)) {
                similar.add(playerName);
            }
        }
        return ImmutableList.copyOf(similar);
    }
}
