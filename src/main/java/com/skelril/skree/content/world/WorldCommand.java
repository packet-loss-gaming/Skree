/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;


import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class WorldCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<WorldProperties> optWorld = args.getOne("world");

        if (!optWorld.isPresent()) {
            src.sendMessage(Text.of("You are in: " + ((Player) src).getWorld().getName() + "."));
            return CommandResult.empty();
        }

        WorldProperties world = optWorld.get();
        ((Player) src).transferToWorld(world.getUniqueId(), world.getSpawnPosition().toDouble());

        src.sendMessage(Text.of(TextColors.YELLOW, "Entered world: " + world.getWorldName() + " successfully!"));
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Teleport to a different world"))
                .permission("skree.world.teleport")
                .arguments(flags().flag("f").buildWith(optional(onlyOne(world(Text.of("world"))))))
                .executor(new WorldCommand()).build();
    }
}