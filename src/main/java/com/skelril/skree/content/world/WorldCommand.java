/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class WorldCommand implements CommandExecutor {

    private Game game;

    public WorldCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Texts.of("You must be a player to use this command!"));
            return CommandResult.empty();
        }

        Optional<WorldProperties> optWorldName = args.getOne("world");

        if (!optWorldName.isPresent()) {
            src.sendMessage(Texts.of("You are in: " + ((Player) src).getWorld().getName() + "."));
            return CommandResult.empty();
        }

        World world = game.getServer().getWorld(optWorldName.get().getUniqueId()).get();

        ((Player) src).setLocationSafely(world.getSpawnLocation());
        src.sendMessage(Texts.of("Entered world: " + world.getName() + " successfully!"));
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Teleport to a different world"))
                .permission("skree.world")
                .arguments(optional(onlyOne(world(Texts.of("world"), game))))
                .executor(new WorldCommand(game)).build();
    }
}