/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.playerstate;

import com.skelril.skree.service.PlayerStateService;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.entity.GameModeData;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.util.HashMap;
import java.util.Map;

import static org.spongepowered.api.util.command.args.GenericArguments.*;

public class GameModeCommand implements CommandExecutor {

    private final PlayerStateService service;

    public GameModeCommand(PlayerStateService service) {
        this.service = service;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        GameMode mode = args.<GameMode>getOne("mode").get();
        Player target = args.<Player>getOne("target").get();

        GameModeData data = target.getGameModeData();
        service.save(target, data.getGameMode().getId());
        target.offer(data.setGameMode(mode));
        service.load(target, data.getGameMode().getId());

        TextBuilder builder = Texts.builder();
        builder.color(TextColors.YELLOW);
        builder.append(Texts.of("Changed game mode to " + mode.getName() + '.'));
        target.sendMessage(builder.build());
        return CommandResult.success();
    }

    public static CommandSpec aquireSpec(Game game, PlayerStateService service) {
        Map<String, GameMode> map = new HashMap<>();

        map.put("survival", GameModes.SURVIVAL);
        map.put("creative", GameModes.CREATIVE);
        map.put("adventure", GameModes.ADVENTURE);
        map.put("spectator", GameModes.SPECTATOR);

        return CommandSpec.builder()
                .description(Texts.of("Change gamemode"))
                .permission("skree.gamemode")
                .arguments(
                        seq(
                                onlyOne(choices(Texts.of("mode"), map)),
                                onlyOne(playerOrSource(Texts.of("target"), game))
                        )
                ).executor(new GameModeCommand(service)).build();
    }
}
