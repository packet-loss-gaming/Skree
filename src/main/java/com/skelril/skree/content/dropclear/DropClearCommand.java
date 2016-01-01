/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.dropclear;


import com.skelril.skree.service.DropClearService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class DropClearCommand implements CommandExecutor {
    private int maxDelay;

    public DropClearCommand(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<DropClearService> optService = Sponge.getServiceManager().provide(DropClearService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The drop clear service is not currently running."));
            return CommandResult.empty();
        }

        DropClearService service = optService.get();

        // World resolution
        Optional<WorldProperties> optWorldProps = args.getOne("world");
        Optional<World> optWorld;

        if (!optWorldProps.isPresent()) {
            if (!(src instanceof Player)) {
                src.sendMessage(Text.of(TextColors.RED, "You are not a player and need to specify a world!"));
                return CommandResult.empty();
            }
            optWorld = Optional.of(((Player) src).getWorld());
        } else {
            optWorld = Sponge.getServer().getWorld(optWorldProps.get().getUniqueId());
        }

        // Handled by command spec, so always provided
        int seconds = args.<Integer>getOne("seconds").get();

        World world = optWorld.get();
        if (!world.isLoaded()) {
            src.sendMessage(Text.of(TextColors.RED, "The specified world was not loaded!"));
            return CommandResult.empty();
        }

        service.cleanup(world, Math.max(0, Math.min(seconds, maxDelay)));

        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(int maxDelay) {
        return CommandSpec.builder()
                .description(Text.of("Trigger a drop clear"))
                .permission("skree.dropclear")
                .arguments(
                        seq(
                                onlyOne(optionalWeak(integer(Text.of("seconds")), 10)),
                                onlyOne(optional(world(Text.of("world"))))
                        )
                ).executor(new DropClearCommand(maxDelay)).build();
    }
}
