/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;

import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LoadWorldCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

        src.sendMessage(Text.of(TextColors.YELLOW, "Loading..."));

        Optional<World> optWorld = service.loadVanillaMapFromDisk(args.<String>getOne("world name").get());

        if (optWorld.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_GREEN, "World loaded successfully."));
        } else {
            src.sendMessage(Text.of(TextColors.RED, "World failed to load."));
        }

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Load a world"))
                .permission("skree.world.load")
                .arguments(GenericArguments.string(Text.of("world name")))
                .executor(new LoadWorldCommand()).build();
    }
}