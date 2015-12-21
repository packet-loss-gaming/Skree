/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.command.sponge;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerSenderProvider implements Provider<Player> {
    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public Player get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        CommandSource sender = arguments.getNamespace().get(CommandSource.class);
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new ArgumentParseException("Only players can use this command.");
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return null;
    }
}
