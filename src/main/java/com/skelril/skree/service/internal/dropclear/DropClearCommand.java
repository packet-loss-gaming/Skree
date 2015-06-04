/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.google.common.base.Optional;
import com.skelril.skree.service.DropClearService;
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

public class DropClearCommand implements CommandExecutor {

    private Game game;
    private DropClearService service;
    private int maxDelay;

    public DropClearCommand(Game game, DropClearService service, int maxDelay) {
        this.game = game;
        this.service = service;
        this.maxDelay = maxDelay;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        // World resolution
        Optional<World> optWorld;
        Optional<String> optWorldName = args.<String>getOne("world");

        if (optWorldName.isPresent()) {
            optWorld = game.getServer().getWorld(optWorldName.get());
        } else if (src instanceof Player) {
            optWorld = Optional.of(((Player) src).getWorld());
        } else {
            src.sendMessage(Texts.of("You are not a player and need to specify a world!"));
            return CommandResult.empty();
        }

        if (!optWorld.isPresent()) {
            src.sendMessage(Texts.of("The specified world could not be found!"));
            return CommandResult.empty();
        }

        // Handled by command spec, so always provided
        int seconds = args.<Integer>getOne("seconds").get();

        service.cleanup(optWorld.get(), Math.max(0, Math.min(seconds, maxDelay)));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game, DropClearService service, int maxDelay) {
        return CommandSpec.builder()
                .description(Texts.of("Trigger a drop clear"))
                .permission("skree.dropclear")
                .arguments(
                        seq(
                                onlyOne(optionalWeak(integer(Texts.of("seconds")), 10)),
                                onlyOne(optional(string(Texts.of("world"))))
                        )
                ).executor(new DropClearCommand(game, service, maxDelay)).build();
    }
}
