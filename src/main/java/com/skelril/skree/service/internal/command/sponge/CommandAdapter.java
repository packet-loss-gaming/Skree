/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.command.sponge;

import com.sk89q.intake.CommandMapping;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.Optional;

public abstract class CommandAdapter implements CommandCallable {
    private CommandMapping command;

    public CommandAdapter(CommandMapping mapping) {
        this.command = mapping;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (String perm : command.getDescription().getPermissions()) {
            if (!source.hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        String description = command.getDescription().getShortDescription();
        if (description != null && !description.isEmpty()) {
            return Optional.of(Texts.of(description));
        }
        return Optional.empty();
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        String help = command.getDescription().getHelp();
        if (help != null && !help.isEmpty()) {
            return Optional.of(Texts.of(help));
        }
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of(command.getDescription().getUsage());
    }
}
