/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;


import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class WorldListCommand implements CommandExecutor {

    private Game game;

    public WorldListCommand(Game game) {
        this.game = game;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Texts.of(TextColors.GOLD, "Available worlds (click to teleport):"));

        Optional<WorldService> service = game.getServiceManager().provide(WorldService.class);

        for (WorldEffectWrapper wrapper : service.get().getEffectWrappers()) {
            String worldType = wrapper.getName();
            for (World world : wrapper.getWorlds()) {
                TextBuilder builder = Texts.builder();
                String worldName = world.getName();
                builder.append(Texts.of(worldName + " [" + worldType + "]"));
                builder.color(TextColors.GREEN);
                builder.onClick(TextActions.runCommand("/world " + worldName));
                builder.onHover(TextActions.showText(Texts.of("Teleport to " + worldName)));
                src.sendMessage(builder.build());
            }
        }
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
            .description(Texts.of("List available worlds"))
            .permission("skree.world")
            .executor(new WorldListCommand(game)).build();
    }
}
