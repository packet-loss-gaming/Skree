/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

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

        Optional<String> optWorldName = args.<String>getOne("world");

        if (!optWorldName.isPresent()) {
            src.sendMessage(Texts.of("You are in: " + ((Player) src).getWorld().getName() + "."));
        }


        String worldName = optWorldName.get();

        Optional<World> world = game.getServer().getWorld(worldName);
        if (!world.isPresent()) {
            src.sendMessage(Texts.of("Failed to find a world named: " + worldName));
            return CommandResult.empty();
        }

        ((Player) src).setLocationSafely(world.get().getSpawnLocation());
        src.sendMessage(Texts.of("Entered world: " + worldName + " successfully!"));
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Teleport to a different world"))
                .permission("skree.world")
                .arguments(optional(onlyOne(string(Texts.of("world"))))).executor(new WorldCommand(game)).build();
    }
}